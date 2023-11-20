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
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.Level;
import com.example.courseservice.data.object.CourseResponseInterface;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.CourseTemporaryRepository;
import com.example.courseservice.data.repositories.CourseTopicRepository;
import com.example.courseservice.data.repositories.LevelRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.CourseMapper;
import com.example.courseservice.mappers.CourseTemporaryMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetmpservice.CourseTmpService;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.services.videotmpservice.VideoTmpService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class CourseTmpServiceImpl implements CourseTmpService {
    @Autowired
    private CourseTemporaryRepository courseTemporaryRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseTopicRepository courseTopicRepository;
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseTopicService courseTopicService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoTmpService videoTmpService;
    @Autowired
    private FileService fileService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private CourseTemporaryMapper courseTemporaryMapper;
    @Autowired
    private PageableUtil pageableUtil;

    @Override
    public void createCourse(CourseRequest courseRequest, MultipartFile thumbnail) {
        FileResponse fileResponse = fileService.fileStorage(thumbnail);
        CloudinaryUrl thumbinial = uploadService.uploadMedia(fileResponse);
        CourseTemporary course = courseTemporaryMapper.mapDtoToEntity(courseRequest);
        course.setThumbnial(thumbinial.getUrl());
        course.setLevelId(courseRequest.getLevelId());
        course.setStatus(CommonStatus.DRAFT);
        course.setCourseTopics(courseTopicService.courseTopicsByString(courseRequest.getTopic()));
        courseTemporaryRepository.save(course);
    }

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
            if (courseUpdateRequest.getVideoOrders() != null && !courseUpdateRequest.getVideoOrders().isEmpty()) {
                videoService.updateVideoOrder(courseUpdateRequest.getVideoOrders(), courseUpdateRequest.getCourseId());
            }
        }
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getCourseTmpAndStatusNot(CommonStatus status, Integer page,
            Integer size, String field,
            SortType sortType) {

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<CourseResponseInterface> courseTemporary = courseTemporaryRepository.getByStatusNot(status, pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseTemporaryMapper.mapInterfacesToDtos(courseTemporary.getContent()))
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
    public void insertUpdateCourseTmpToReal(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        CourseTemporary courseTemporary = courseTemporaryRepository
                .findByCourseId(courseId)
                .orElseThrow(() -> new BadRequestException("Cannot found course update with id " + courseId));
        course = courseTemporaryMapper.mapCoursetmpToCourse(course, courseTemporary);
        courseRepository.save(course);
        courseTemporaryRepository.delete(courseTemporary);
    }

    @Override
    public CourseDetailResponse getCourseDetail(Long id) {
        CourseTemporary courseTemporary = courseTemporaryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "Not found course temporary with id " + id + " in getCourseDetail temporary function"));
        return courseTemporaryMapper.mapCourseDetailResponse(courseTemporary);
    }

    @Override
    public void insertCourseTmpToReal(Long courseTeporaryId) {
        CourseTemporary courseTemporary = courseTemporaryRepository.findById(courseTeporaryId)
                .orElseThrow(() -> new BadRequestException("Cannot found course temporary with id " + courseTeporaryId
                        + " in function insertCourseTmpToReal"));
        Level level = levelRepository.findById(courseTemporary.getLevelId())
                .orElseThrow(() -> new BadRequestException("Cannot found level with id " + courseTemporary.getLevelId()
                        + " in function insertCourseTmpToReal"));

        Course course = courseTemporaryMapper.mapToCourse(courseTemporary);
        course.setCommonStatus(CommonStatus.AVAILABLE);
        course.setLevel(level);
        course = courseRepository.save(course);

        courseTopicRepository.updateCourseIdByCourseTemporaryId(courseTeporaryId, course.getId());
        videoTmpService.insertVideoTmpToReal(courseTeporaryId);
    }

    @Override
    public void rejectCourse(Long courseTmpId) {
        CourseTemporary courseTemporary = courseTemporaryRepository.findById(courseTmpId)
                .orElseThrow(() -> new BadRequestException("Cannot found course temporary with id " + courseTmpId
                        + " in function rejectCourse"));
        courseTemporary.setStatus(CommonStatus.REJECT);
        courseTemporaryRepository.save(courseTemporary);
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getCourseTmpByEmailAndStatusNot(CommonStatus status,
            Integer page, Integer size, String field, SortType sortType) {
        String email = securityContextService.getCurrentUser().getEmail();
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<CourseResponseInterface> courseTemporary = courseTemporaryRepository.getByEmailAndStatusNot(email, status,
                pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseTemporaryMapper.mapInterfacesToDtos(courseTemporary.getContent()))
                .totalPage(courseTemporary.getTotalPages())
                .totalRow(courseTemporary.getTotalElements())
                .build();
    }

}
