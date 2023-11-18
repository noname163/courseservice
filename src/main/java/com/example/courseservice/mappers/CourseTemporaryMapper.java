package com.example.courseservice.mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;

@Component
public class CourseTemporaryMapper {
    public Course mapCoursetmpToCourse(Course course, CourseTemporary courseTemporary) {
        course.setName(courseTemporary.getName());
        course.setDescription(courseTemporary.getDescription());
        course.setPrice(courseTemporary.getPrice());
        course.setSubject(courseTemporary.getSubject());
        course.setUpdateTime(courseTemporary.getUpdateTime());
        course.setThumbnial(courseTemporary.getThumbnial());
        course.setCommonStatus(courseTemporary.getStatus());
        return course;
    }

    public CourseTemporary mapDtoToEntity(CourseUpdateRequest courseUpdateRequest, Course course) {
        return CourseTemporary.builder()
                .description(Optional.ofNullable(courseUpdateRequest.getDescription())
                        .orElse(course.getDescription()))
                .price(Optional.ofNullable(courseUpdateRequest.getPrice()).filter(price -> price > 0)
                        .orElse(course.getPrice()))
                .levelId(Optional.ofNullable(courseUpdateRequest.getLevelId())
                        .filter(levelId -> levelId > 0)
                        .orElse(course.getLevel().getId()))
                .name(Optional.ofNullable(courseUpdateRequest.getName()).orElse(course.getName()))
                .status(CommonStatus.UPDATING)
                .course(course).build();
    }

    public CourseResponse mapCourseTmpToCourseResponse(CourseTemporary course){
        return CourseResponse
                .builder()
                .id(course.getCourse().getId())
                .price(course.getPrice())
                .createdDate(course.getCourse().getCreateDate())
                .updateDate(course.getUpdateTime())
                .status(course.getStatus())
                .courseName(course.getName())
                .teacherName(course.getCourse().getTeacherName())
                .thumbnial(course.getThumbnial())
                .subject(course.getSubject())
                .totalVideo(course.getCourse().getVideos().size())
                .build();
    }

    public List<CourseResponse> mapCoursesTmpToCourseResponses(List<CourseTemporary> courses) {
        return courses.stream().map(this::mapCourseTmpToCourseResponse).collect(Collectors.toList());
    }

    public CourseTemporary mapCourseTemporary(CourseTemporary courseTemporary, CourseUpdateRequest courseUpdateRequest,
            Course course) {
        courseTemporary.setCourse(course);
        courseTemporary.setDescription(Optional.ofNullable(courseUpdateRequest.getDescription())
                .orElse(course.getDescription()));
        courseTemporary.setPrice(Optional.ofNullable(courseUpdateRequest.getPrice()).filter(price -> price > 0)
                .orElse(course.getPrice()));
        courseTemporary.setLevelId(Optional.ofNullable(courseUpdateRequest.getLevelId())
                .filter(levelId -> levelId > 0)
                .orElse(course.getLevel().getId()));
        courseTemporary.setName(Optional.ofNullable(courseUpdateRequest.getName()).orElse(course.getName()));
        courseTemporary.setStatus(CommonStatus.UPDATING);
        return courseTemporary;
    }
}
