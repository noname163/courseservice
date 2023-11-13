package com.example.courseservice.services.studentenrollcourseservice.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.StudentEnrolledCourses;
import com.example.courseservice.data.repositories.StudentEnrolledCoursesRepository;
import com.example.courseservice.mappers.CourseMapper;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class StudentEnrollCourseServiceImpl implements StudentEnrollCourseService {
    @Autowired
    private StudentEnrolledCoursesRepository studentEnrolledCoursesRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private PageableUtil pageableUtil;

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourse(String email, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<StudentEnrolledCourses> studentEnrollCourse = studentEnrolledCoursesRepository.findByStudentEmail(email,
                pageable);
        List<CourseResponse> courseResponses = studentEnrollCourse.getContent()
                .stream()
                .map(studentEnrolledCourse -> courseMapper.mapEntityToDto(studentEnrolledCourse.getCourse()))
                .collect(Collectors.toList());

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseResponses)
                .totalPage(studentEnrollCourse.getTotalPages())
                .totalRow(studentEnrollCourse.getTotalElements())
                .build();
    }

}
