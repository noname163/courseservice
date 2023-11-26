package com.example.courseservice.mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.CourseTemporaryUpdateRequest;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.object.CourseResponseInterface;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.services.authenticationservice.SecurityContextService;

@Component
public class CourseTemporaryMapper {

    @Autowired
    private SecurityContextService securityContextService;

    public Course mapCoursetmpToCourse(Course course, CourseTemporary courseTemporary) {
        course.setName(Optional.ofNullable(courseTemporary.getName()).orElse(course.getName()));
        course.setDescription(Optional.ofNullable(courseTemporary.getDescription()).orElse(course.getDescription()));
        course.setPrice(Optional.ofNullable(courseTemporary.getPrice()).orElse(course.getPrice()));
        course.setSubject(Optional.ofNullable(courseTemporary.getSubject()).orElse(course.getSubject()));
        course.setUpdateTime(courseTemporary.getUpdateTime());
        course.setUpdateTime(LocalDateTime.now());
        course.setThumbnial(Optional.ofNullable(courseTemporary.getThumbnial()).orElse(course.getThumbnial()));
        course.setCommonStatus(courseTemporary.getStatus());
        return course;
    }

    public CourseTemporary mapDtoToEntity(CourseRequest courseRequest) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        return CourseTemporary
                .builder()
                .subject(courseRequest.getSubject().getName())
                .subjectId(courseRequest.getSubject().getId())
                .price(courseRequest.getPrice())
                .teacherId(currentUser.getId())
                .name(courseRequest.getName())
                .teacherName(currentUser.getFullname())
                .teacherEmail(currentUser.getEmail())
                .teacherAvatar(currentUser.getAvatar())
                .teacherId(currentUser.getId())
                .description(courseRequest.getDescription())
                .build();
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
                .status(CommonStatus.DRAFT)
                .course(course).build();
    }

    public CourseResponse mapCourseTmpToCourseResponse(CourseTemporary course) {
        return CourseResponse
                .builder()
                .id(course.getId())
                .teacherAvatar(course.getTeacherAvatar())
                .price(course.getPrice())
                .createdDate(course.getCreateDate())
                .updateDate(course.getUpdateTime())
                .status(course.getStatus())
                .courseName(course.getName())
                .teacherName(course.getTeacherName())
                .thumbnial(course.getThumbnial())
                .subject(course.getSubject())
                .build();
    }

    public List<CourseResponse> mapCoursesTmpToCourseResponses(List<CourseTemporary> courses) {
        return courses.stream().map(this::mapCourseTmpToCourseResponse).collect(Collectors.toList());
    }

    public CourseTemporary mapCourseTemporary(CourseTemporary courseTemporary,
            CourseUpdateRequest courseUpdateRequest,
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

    public CourseDetailResponse mapCourseDetailResponse(CourseTemporary courseTemporary) {
        return CourseDetailResponse
                .builder()
                .id(courseTemporary.getId())
                .name(courseTemporary.getName())
                .createdDate(courseTemporary.getCreateDate())
                .updateDate(courseTemporary.getUpdateTime())
                .status(courseTemporary.getStatus())
                .price(courseTemporary.getPrice())
                .subject(courseTemporary.getSubject())
                .teacherName(courseTemporary.getTeacherName())
                .thumbnail(courseTemporary.getThumbnial())
                .description(courseTemporary.getDescription())
                .build();
    }

    public Course mapToCourse(CourseTemporary courseTemporary) {
        return Course
                .builder()
                .name(courseTemporary.getName())
                .createDate(courseTemporary.getCreateDate())
                .updateTime(courseTemporary.getUpdateTime())
                .price(courseTemporary.getPrice())
                .thumbnial(courseTemporary.getThumbnial())
                .description(courseTemporary.getDescription())
                .subject(courseTemporary.getSubject())
                .subjectId(courseTemporary.getSubjectId())
                .teacherEmail(courseTemporary.getTeacherEmail())
                .teacherId(courseTemporary.getTeacherId())
                .teacherName(courseTemporary.getTeacherName())
                .build();
    }

    public CourseResponse mapInterfaceToReal(CourseResponseInterface courseResponseInterface) {
        return CourseResponse
                .builder()
                .id(courseResponseInterface.getId())
                .courseName(courseResponseInterface.getCourseName())
                .createdDate(courseResponseInterface.getCreatedDate())
                .updateDate(courseResponseInterface.getUpdateDate())
                .level(courseResponseInterface.getLevel())
                .price(courseResponseInterface.getPrice())
                .status(courseResponseInterface.getStatus())
                .subject(courseResponseInterface.getSubject())
                .teacherName(courseResponseInterface.getTeacherName())
                .thumbnial(courseResponseInterface.getThumbnial())
                .totalVideo(courseResponseInterface.getTotalVideo())
                .build();
    }

    public List<CourseResponse> mapInterfacesToDtos(List<CourseResponseInterface> courses) {
        return courses.stream().map(this::mapInterfaceToReal).collect(Collectors.toList());
    }

    public CourseTemporary mapUpdateTemporaryToCourseTemporary(
            CourseTemporaryUpdateRequest courseTemporaryUpdateRequest, CourseTemporary courseUpdateRequest) {
        courseUpdateRequest.setName(
                Optional.ofNullable(courseTemporaryUpdateRequest.getName()).orElse(courseUpdateRequest.getName()));
        courseUpdateRequest.setDescription(Optional.ofNullable(courseTemporaryUpdateRequest.getDescription())
                .orElse(courseUpdateRequest.getDescription()));
        courseUpdateRequest.setLevelId(Optional.ofNullable(courseTemporaryUpdateRequest.getLevelId())
                .orElse(courseUpdateRequest.getLevelId()));
        courseUpdateRequest.setPrice(
                Optional.ofNullable(courseTemporaryUpdateRequest.getPrice()).orElse(courseUpdateRequest.getPrice()));
        courseUpdateRequest.setSubject(Optional.ofNullable(courseTemporaryUpdateRequest.getSubject().getName())
                .orElse(courseUpdateRequest.getSubject()));
        courseUpdateRequest.setSubjectId(Optional.ofNullable(courseTemporaryUpdateRequest.getSubject().getId())
                .orElse(courseUpdateRequest.getSubjectId()));
        return courseUpdateRequest;
    }
}
