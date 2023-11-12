package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.services.authenticationservice.SecurityContextService;

@Component
public class CourseMapper {
    @Autowired
    private SecurityContextService securityContextService;

    public Course mapDtoToEntity(CourseRequest courseRequest) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        return Course
                .builder()
                .subject(courseRequest.getSubject())
                .price(courseRequest.getPrice())
                .name(courseRequest.getName())
                .teacherName(currentUser.getFullname())
                .teacherEmail(currentUser.getEmail())
                .description(courseRequest.getDescription())
                .build();
    }

    public CourseDetailResponse mapCourseDetailEntityToDto(Course course) {
        return CourseDetailResponse
                .builder()
                .createDate(course.getCreateDate())
                .description(course.getDescription())
                .updateDate(course.getUpdateTime())
                .totalStudent(course.getStudentEnrolledCourses().size())
                .build();
    }

    public CourseResponse mapEntityToDto(Course course) {
        return CourseResponse
                .builder()
                .id(course.getId())
                .price(course.getPrice())
                .level(course.getLevel().getName())
                .courseName(course.getName())
                .teacherName(course.getTeacherName())
                .thumbnial(course.getThumbnial())
                .rating(course.getAverageRating())
                .numberOfRate(course.getRatings().size())
                .subject(course.getSubject())
                .totalVideo(course.getVideos().size())
                .build();
    }

    public List<CourseResponse> mapEntitiesToDtos(List<Course> courses) {
        return courses.stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }
}
