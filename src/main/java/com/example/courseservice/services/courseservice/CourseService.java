package com.example.courseservice.services.courseservice;

import java.util.List;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;

public interface CourseService {
    public void createCourse(CourseRequest courseRequest);
    public PaginationResponse<List<CourseResponse>> getListCourseByEmail(String email, Integer page, Integer size, String field, SortType sortType);
    public PaginationResponse<List<CourseResponse>> getListCourse(Integer page, Integer size, String field, SortType sortType);
}
