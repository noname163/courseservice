package com.example.courseservice.services.studentenrollcourseservice;

import java.util.List;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;

public interface StudentEnrollCourseService {
    public PaginationResponse<List<CourseResponse>> getListCourse(Integer page,
            Integer size, String field, SortType sortType);
    public boolean isStudentEnrolled(String studentEmail, Long CourseId);

    public void insertStudentEnroll(Long courseId);
}
