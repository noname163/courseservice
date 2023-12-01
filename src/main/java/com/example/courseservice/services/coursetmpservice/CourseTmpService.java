package com.example.courseservice.services.coursetmpservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.CourseTemporaryUpdateRequest;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;

public interface CourseTmpService {
    public void createCourse(CourseRequest courseRequest, MultipartFile thumbnail);
    public void updateRealCourse(CourseUpdateRequest courseUpdateRequest, MultipartFile thumbnail);
    public void editTmpCourse(CourseTemporaryUpdateRequest courseUpdateRequest, MultipartFile thumbnail);
    public PaginationResponse<List<CourseResponse>> getCourseTmpAndStatusNot(List<CommonStatus> status,Integer page,
            Integer size, String field, SortType sortType);
    public PaginationResponse<List<CourseResponse>> searchTemporaryCourseForTeacher(String searchTerm,Integer page,
            Integer size, String field, SortType sortType);
    public PaginationResponse<List<CourseResponse>> getCourseTmpByEmailAndStatusNot(CommonStatus status,Integer page,
            Integer size, String field, SortType sortType);
    public boolean isUpdate(Long courseId);
    public void insertUpdateCourseTmpToReal(Long courseId);
    public Long insertCourseTmpToReal(VerifyRequest courseTeporaryId);
    public CourseDetailResponse getCourseDetail(Long id);
    public void requestVerifyCourses(List<Long> courseIds);
    public void rejectCourse(VerifyRequest actionRequest);
    public CourseTemporary createNewCourseTemporaryByCourse(Course course);
    public void deleteDraftCourse(Long id);
    public void updateVideoOrders(List<VideoOrder> videoOrders, Long courseId, Long courseTemporaryId);
}
