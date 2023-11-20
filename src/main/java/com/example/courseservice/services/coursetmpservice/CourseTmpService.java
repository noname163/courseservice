package com.example.courseservice.services.coursetmpservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;

public interface CourseTmpService {
    public void createCourse(CourseRequest courseRequest, MultipartFile thumbnail);
    public void insertTmpCourse(CourseUpdateRequest courseUpdateRequest, MultipartFile thumbnail);
    public PaginationResponse<List<CourseResponse>> getUpdateCourse(Integer page,
            Integer size, String field, SortType sortType);
    public boolean isUpdate(Long courseId);
    public void insertUpdateCourseTmpToReal(Long courseId);
    public void insertCourseTmpToReal(Long courseTeporaryId);
    public CourseDetailResponse getCourseDetail(Long id);
    public void rejectCourse(Long courseTmpId);
}
