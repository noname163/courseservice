package com.example.courseservice.services.coursetmpservice.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.CourseTemporaryRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.CourseTemporaryMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetmpservice.CourseTmpService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.utils.PageableUtil;
import com.netflix.discovery.converters.Auto;

@Service
public class CourseTmpServiceImpl implements CourseTmpService {
    @Autowired
    private CourseTemporaryRepository courseTemporaryRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseService courseService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private FileService fileService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private CourseTemporaryMapper courseTemporaryMapper;
    @Autowired
    private PageableUtil pageableUtil;

    @Override
    public void insertTmpCourse(CourseUpdateRequest courseUpdateRequest, MultipartFile thumbnail) {

        UserInformation currentUser = securityContextService.getCurrentUser();
        Course course = courseService.getCourseByIdAndEmail(courseUpdateRequest.getCourseId(),
                currentUser.getEmail());
        CloudinaryUrl thumbnial = null;
        if (thumbnail != null) {
            FileResponse fileResponse = fileService.fileStorage(thumbnail);
            thumbnial = uploadService.uploadMedia(fileResponse);
        }
        Optional<CourseTemporary> existCourseTemporaryOtp = courseTemporaryRepository
                .findByCourseId(courseUpdateRequest.getCourseId());
        if (existCourseTemporaryOtp.isEmpty()) {
            CourseTemporary courseTemporary = courseTemporaryMapper.mapDtoToEntity(courseUpdateRequest, course);
            courseTemporary.setThumbnial(thumbnial != null ? thumbnial.getUrl() : course.getThumbnial());
            courseTemporaryRepository.save(courseTemporary);
        } else {
            CourseTemporary courseTemporary = existCourseTemporaryOtp.get();
            courseTemporary = courseTemporaryMapper.mapCourseTemporary(courseTemporary, courseUpdateRequest, course);
            courseTemporary.setThumbnial(thumbnial != null ? thumbnial.getUrl() : course.getThumbnial());
            courseTemporaryRepository.save(courseTemporary);
            if(courseUpdateRequest.getVideoOrders()!=null && !courseUpdateRequest.getVideoOrders().isEmpty()){
                videoService.updateVideoOrder(courseUpdateRequest.getVideoOrders(), courseUpdateRequest.getCourseId());
            }
        }
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getUpdateCourse(Integer page, Integer size, String field,
            SortType sortType) {

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<CourseTemporary> courseTemporary = courseTemporaryRepository.findAll(pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseTemporaryMapper.mapCoursesTmpToCourseResponses(courseTemporary.getContent()))
                .totalPage(courseTemporary.getTotalPages())
                .totalRow(courseTemporary.getTotalElements())
                .build();
    }

    @Override
    public boolean isUpdate(Long courseId) {
        Optional<CourseTemporary> courseTemporary = courseTemporaryRepository.findByCourseId(courseId);
        if (courseTemporary.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void inserCourseTmpToReal(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        CourseTemporary courseTemporary = courseTemporaryRepository
                .findByCourseId(courseId)
                .orElseThrow(() -> new BadRequestException("Cannot found course update with id " + courseId));
        course = courseTemporaryMapper.mapCoursetmpToCourse(course, courseTemporary);
        courseRepository.save(course);
        courseTemporaryRepository.delete(courseTemporary);
    }

}
