package com.example.courseservice.services.coursetmpservice;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.request.CourseUpdateRequest;

public interface CourseTmpService {
    public void insertTmpCourse(CourseUpdateRequest courseUpdateRequest, MultipartFile thumbnail);
}
