package com.example.courseservice.services.courseservice;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.CourseFilter;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;

public interface CourseService {
    public void createCourse(CourseRequest courseRequest, MultipartFile thumbnail);
    public CourseDetailResponse getCourseDetail(long id, CommonStatus commonStatus);
    public CourseDetailResponse getCourseDetailExcept(long id, CommonStatus commonStatus);
    public PaginationResponse<List<CourseResponse>> getListCourseByEmail(Integer page, Integer size, String field, SortType sortType);
    public PaginationResponse<List<CourseResponse>> getListCourseByEmailForUser(String email,Integer page, Integer size, String field, SortType sortType);
    public PaginationResponse<List<CourseResponse>> filterCourseBy(CourseFilter filterBy,CommonStatus commonStatus,List<String> value, Integer page, Integer size, String field, SortType sortType);
    public PaginationResponse<List<CourseResponse>> getListCourse(CommonStatus commonStatus, Integer page, Integer size, String field, SortType sortType);
    public void verifyCourse(VerifyRequest verifyRequest);
    public Course getCourseByIdAndEmail(Long id, String email);
    public Course getCourseById(Long id);
    public boolean isCourseBelongTo(String email, long courseId);
    public void deleteCourse(Long courseId);
    public PaginationResponse<List<CourseResponse>> searchCourse(String searchTerm, Integer page, Integer size, String field, SortType sortType);
    public PaginationResponse<List<CourseResponse>> getAllCourse(Integer page, Integer size, String field, SortType sortType);
}
