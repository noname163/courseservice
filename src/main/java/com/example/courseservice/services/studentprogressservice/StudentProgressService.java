package com.example.courseservice.services.studentprogressservice;

import java.util.List;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;

public interface StudentProgressService {
    public PaginationResponse<List<CourseResponse>> getCourseOfCurrentUser(Integer page,
            Integer size, String field, SortType sortType);

    public void createStudentProgressByVideoId(Long videoId);

    public List<CourseResponse> setStudentProgressForListCourseResponse(List<CourseResponse> courseResponses);
}
