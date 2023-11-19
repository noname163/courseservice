package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.object.CourseDetailResponseInterface;
import com.example.courseservice.data.object.CourseResponseInterface;
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
                .subject(courseRequest.getSubject().getName())
                .subjectId(courseRequest.getSubject().getId())
                .price(courseRequest.getPrice())
                .teacherId(currentUser.getId())
                .name(courseRequest.getName())
                .teacherName(currentUser.getFullname())
                .teacherEmail(currentUser.getEmail())
                .description(courseRequest.getDescription())
                .build();
    }

    public CourseDetailResponse mapCourseDetailEntityToDto(Course course) {
        return CourseDetailResponse
                .builder()
                .id(course.getId())
                .description(course.getDescription())
                .totalStudent(course.getStudentEnrolledCourses().size())
                .build();
    }

    public CourseDetailResponse mapToCourseDetailResponse(CourseDetailResponseInterface courseDetailResponseInterface) {
        if (courseDetailResponseInterface == null) {
            return null;
        }

        return CourseDetailResponse.builder()
                .id(courseDetailResponseInterface.getId())
                .totalStudent(courseDetailResponseInterface.getTotalStudent())
                .description(courseDetailResponseInterface.getDescription())
                .name(courseDetailResponseInterface.getName())
                .thumbnail(courseDetailResponseInterface.getthumbnail())
                .teacherName(courseDetailResponseInterface.getTeacherName())
                .rating(courseDetailResponseInterface.getAverageRating())
                .numberOfRate(courseDetailResponseInterface.getNumberOfRate())
                .totalVideo(courseDetailResponseInterface.getTotalVideo())
                .subject(courseDetailResponseInterface.getSubject())
                .level(courseDetailResponseInterface.getLevel())
                .price(courseDetailResponseInterface.getPrice())
                .createdDate(courseDetailResponseInterface.getCreatedDate())
                .updateDate(courseDetailResponseInterface.getUpdateDate())
                .status(courseDetailResponseInterface.getStatus())
                .build();
    }

    public CourseResponse mapEntityToDto(Course course) {
        return CourseResponse
                .builder()
                .id(course.getId())
                .price(course.getPrice())
                .createdDate(course.getCreateDate())
                .updateDate(course.getUpdateTime())
                .status(course.getCommonStatus())
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

    public CourseResponse mapInterfaceToReal(CourseResponseInterface courseResponseInterface) {
        return CourseResponse
                .builder()
                .id(courseResponseInterface.getId())
                .rating(courseResponseInterface.getAverageRating())
                .courseName(courseResponseInterface.getCourseName())
                .createdDate(courseResponseInterface.getCreatedDate())
                .updateDate(courseResponseInterface.getUpdateDate())
                .level(courseResponseInterface.getLevel())
                .numberOfRate(courseResponseInterface.getNumberOfRate())
                .price(courseResponseInterface.getPrice())
                .status(courseResponseInterface.getStatus())
                .subject(courseResponseInterface.getSubject())
                .teacherName(courseResponseInterface.getTeacherName())
                .thumbnial(courseResponseInterface.getThumbnial())
                .totalVideo(courseResponseInterface.getTotalVideo())
                .build();
    }

    public List<CourseResponse> mapEntitiesToDtos(List<Course> courses) {
        return courses.stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }
    public List<CourseResponse> mapInterfacesToDtos(List<CourseResponseInterface> courses) {
        return courses.stream().map(this::mapInterfaceToReal).collect(Collectors.toList());
    }
}
