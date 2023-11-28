package com.example.courseservice.services.courseservice.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.CourseFilter;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.VerifyStatus;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Level;
import com.example.courseservice.data.object.CourseDetailResponseInterface;
import com.example.courseservice.data.object.CourseResponseInterface;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.CourseTemporaryRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.CourseMapper;
import com.example.courseservice.mappers.CourseTemporaryMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetmpservice.CourseTmpService;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.levelservice.LevelService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private UploadService uploadService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private CourseTopicService courseTopicService;
    @Autowired
    private CourseTmpService courseTmpService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private StudentEnrollCourseService studentEnrollCourseService;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private CourseTemporaryRepository courseTemporaryRepository;
    @Autowired
    private CourseTemporaryMapper courseTemporaryMapper;

    private final Double MAXRATE = 5d;

    @Override
    public void createCourse(CourseRequest courseRequest, MultipartFile thumbnail) {
        Level level = levelService.getLevel(courseRequest.getLevelId());
        FileResponse fileResponse = fileService.fileStorage(thumbnail);
        CloudinaryUrl thumbinial = uploadService.uploadMedia(fileResponse);
        Course course = courseMapper.mapDtoToEntity(courseRequest);
        course.setThumbnial(thumbinial.getUrl());
        course.setLevel(level);
        course.setCommonStatus(CommonStatus.WAITING);
        course.setCourseTopics(courseTopicService.courseTopicsByString(courseRequest.getTopic()));
        courseRepository.save(course);
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourseByEmail(Integer page, Integer size,
            String field, SortType sortType) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<CourseResponseInterface> listSubject = courseRepository.getCourseByEmail(currentUser.getEmail(), pageable);
        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapInterfacesToDtos(listSubject.getContent()))
                .totalPage(listSubject.getTotalPages())
                .totalRow(listSubject.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourse(CommonStatus commonStatus, Integer page, Integer size,
            String field,
            SortType sortType) {

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        if (Boolean.TRUE.equals(securityContextService.getLoginStatus())&&securityContextService.getCurrentUser().getRole().equals("STUDENT")) {
            PaginationResponse<List<CourseResponse>> result = getCourseWhenUserLogin(
                    securityContextService.getCurrentUser().getEmail(), page, size, field,
                    sortType);
            if (!result.getData().isEmpty()) {
                return result;
            }
        }

        if (commonStatus.equals(CommonStatus.ALL)) {
            Page<CourseResponseInterface> listCourse = courseRepository.findByAllCommonStatus(pageable);
            return PaginationResponse.<List<CourseResponse>>builder()
                    .data(courseMapper.mapInterfacesToDtos(listCourse.getContent()))
                    .totalPage(listCourse.getTotalPages())
                    .totalRow(listCourse.getTotalElements())
                    .build();
        }

        Page<CourseResponseInterface> listCourse = courseRepository.getByCommonStatusJPQL(commonStatus, pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapInterfacesToDtos(listCourse.getContent()))
                .totalPage(listCourse.getTotalPages())
                .totalRow(listCourse.getTotalElements())
                .build();
    }

    @Override
    public CourseDetailResponse getCourseDetail(long id, CommonStatus commonStatus) {
        CourseDetailResponseInterface result;
        if (Boolean.TRUE.equals(securityContextService.getLoginStatus())) {
            result = courseRepository
                    .getCourseDetailsByCourseIdAndStatusNot(id, CommonStatus.BANNED);
        } else {
            result = courseRepository
                    .getCourseDetailsByCourseId(id, commonStatus);
        }
        if (result == null) {
            throw new BadRequestException("Not found course with id " + id);
        }
        CourseDetailResponse courseDetailResponse = courseMapper.mapToCourseDetailResponse(result);
        List<CourseVideoResponse> videos = videoService.getVideoByCourseIdAndCommonStatus(id, commonStatus);
        List<String> topics = courseTopicService.getTopicsByCourseId(id);
        courseDetailResponse.setCourseVideoResponses(videos);
        courseDetailResponse.setTopics(topics);
        return courseDetailResponse;
    }

    @Override
    public PaginationResponse<List<CourseResponse>> filterCourseBy(CourseFilter filterBy, CommonStatus commonStatus,
            List<String> value,
            Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        PaginationResponse<List<CourseResponse>> result = PaginationResponse.<List<CourseResponse>>builder().build();
        switch (filterBy) {
            case SUBJECT:
                Page<Course> course = courseRepository.findByCommonStatusAndSubjectIn(pageable, commonStatus,
                        value);
                result.setData(courseMapper.mapEntitiesToDtos(course.getContent()));
                result.setTotalPage(course.getTotalPages());
                result.setTotalRow(course.getNumberOfElements());
                break;
            case RATE:
                result = filterCourseByRate(value, commonStatus, pageable);
                break;
            case PRICE:
                result = filterByPrice(value, commonStatus, pageable);
                break;
            case LEVEL:
                result = filterCourseByLevel(value, commonStatus, pageable);
                break;
            default:
                result = getListCourse(commonStatus, page, size, field, sortType);
                break;
        }
        return result;
    }

    private PaginationResponse<List<CourseResponse>> filterCourseByRate(List<String> value, CommonStatus commonStatus,
            Pageable pageable) {
        if (value == null || value.isEmpty() || value.size() > 1) {
            throw new BadRequestException("Invalid data require for filter by rate");
        }
        Double minRate = Double.parseDouble(value.stream().findFirst().get());
        if (minRate < 0) {
            throw new BadRequestException("Rate cannot smaller than 0");
        }
        Page<Course> courses = courseRepository.findByCommonStatusAndAverageRateBetween(commonStatus, minRate,
                MAXRATE, pageable);
        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(courses.getContent()))
                .totalPage(courses.getTotalPages())
                .totalRow(courses.getTotalElements())
                .build();
    }

    private PaginationResponse<List<CourseResponse>> filterCourseByLevel(List<String> value, CommonStatus commonStatus,
            Pageable pageable) {
        if (value.size() > 3) {
            throw new BadRequestException("Invalid data require for filter by rate");
        }
        List<Long> levelIds = value
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Level> levels = levelService.getListLevel(levelIds);
        Page<Course> courses = courseRepository.findByCommonStatusAndLevelIn(pageable, commonStatus, levels);
        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(courses.getContent()))
                .totalPage(courses.getTotalPages())
                .totalRow(courses.getTotalElements())
                .build();
    }

    private PaginationResponse<List<CourseResponse>> filterByPrice(List<String> value, CommonStatus commonStatus,
            Pageable pageable) {
        if (value.size() > 2) {
            throw new BadRequestException("Invalid data for filter by price");
        }
        Double price1 = Double.parseDouble(value.get(0));
        Double price2 = Double.parseDouble(value.get(1));

        Double min = Math.min(price1, price2);
        Double max = Math.max(price1, price2);
        Page<Course> courses = courseRepository.findByCommonStatusAndPriceBetween(commonStatus, min,
                max, pageable);
        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(courses.getContent()))
                .totalPage(courses.getTotalPages())
                .totalRow(courses.getTotalElements())
                .build();

    }

    @Override
    public void verifyCourse(VerifyRequest verifyRequest) {
        if (verifyRequest.getVerifyStatus().equals(VerifyStatus.ACCEPTED)) {
            courseTmpService.insertCourseTmpToReal(verifyRequest);
        } else {
            courseTmpService.rejectCourse(verifyRequest);
        }
    }

    @Override
    public Course getCourseByIdAndEmail(Long id, String email) {
        return courseRepository
                .findCourseByTeacherEmailAndId(email, id)
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found course belong to email " + email + " with id " + id));
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found course with id " + id));
    }

    @Override
    public boolean isCourseBelongTo(String email, long courseId) {
        return courseRepository.existsByTeacherEmailAndId(email, courseId);
    }

    @Override
    public CourseDetailResponse getCourseDetailExcept(long id, CommonStatus commonStatus) {
        CourseDetailResponseInterface course = courseRepository
                .getCourseDetailsByCourseIdIgnoreStatus(id);
        CourseDetailResponse courseDetailResponse = courseMapper.mapToCourseDetailResponse(course);
        List<CourseVideoResponse> videos = videoService.getVideoByCourseIdAndCommonStatus(id, CommonStatus.ALL);
        courseDetailResponse.setCourseVideoResponses(videos);
        return courseDetailResponse;

    }

    private PaginationResponse<List<CourseResponse>> getCourseWhenUserLogin(String email, Integer page,
            Integer size, String field, SortType sortType) {
        List<Long> coursesId = studentEnrollCourseService.getListCourseId(email);

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<CourseResponseInterface> courses = courseRepository.getAvailableCoursesByCommonStatusNotAndNotInList(
                CommonStatus.BANNED, coursesId,
                pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapInterfacesToDtos(courses.getContent()))
                .totalPage(courses.getTotalPages())
                .totalRow(courses.getTotalElements())
                .build();
    }

    @Override
    public void deleteCourse(Long courseId) {
        String email = securityContextService.getCurrentUser().getEmail();
        Course course = courseRepository.findCourseByTeacherEmailAndId(email, courseId).orElseThrow(
                () -> new BadRequestException("Cannot found course with id " + courseId + " in function deleteCourse"));
        course.setCommonStatus(CommonStatus.DELETED);
        courseRepository.save(course);
    }

    @Override
    public PaginationResponse<List<CourseResponse>> searchCourse(String searchTerm, Integer page, Integer size,
            String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<CourseResponseInterface> courseResponseInterface = courseRepository.searchCourses(searchTerm, pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapInterfacesToDtos(courseResponseInterface.getContent()))
                .totalPage(courseResponseInterface.getTotalPages())
                .totalRow(courseResponseInterface.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getAllCourse(Integer page, Integer size, String field,
            SortType sortType) {

        String email = securityContextService.getCurrentUser().getEmail();
        Pageable pageableStart = pageableUtil.getPageable(page, size, field, sortType);

        // Fetch data from both repositories
        Page<CourseResponseInterface> courseTmpResponseInterface = courseTemporaryRepository
                .getByEmailAndStatusNot(email, CommonStatus.DELETED, pageableStart);
        int remainSize = size - courseTmpResponseInterface.getContent().size();
        if (remainSize == 0) {
            return PaginationResponse.<List<CourseResponse>>builder()
                    .data(courseTemporaryMapper.mapInterfacesToDtos(courseTmpResponseInterface.getContent()))
                    .totalPage(courseTmpResponseInterface.getTotalPages())
                    .totalRow(courseTmpResponseInterface.getTotalElements())
                    .build();
        }
        Pageable pageableEnd = pageableUtil.getPageable(page, remainSize, field, sortType);
        Page<CourseResponseInterface> courseResponseInterface = courseRepository.getCourseByEmail(email,
                pageableEnd);

        // Map data to DTOs
        List<CourseResponse> courseResponse = courseMapper.mapInterfacesToDtos(courseResponseInterface.getContent());
        List<CourseResponse> courseTemporaryResponse = courseTemporaryMapper
                .mapInterfacesToDtos(courseTmpResponseInterface.getContent());

        // Concatenate the two lists
        List<CourseResponse> result = new ArrayList<>(courseTemporaryResponse);
        result.addAll(courseResponse);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(result)
                .totalPage(courseTmpResponseInterface.getTotalPages() + courseResponseInterface.getTotalPages())
                .totalRow(courseResponseInterface.getTotalElements() + courseTmpResponseInterface.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourseByEmailForUser(String email, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        List<Long> enrolled = new ArrayList<>();
        if (Boolean.TRUE.equals(securityContextService.getLoginStatus())) {
            enrolled = studentEnrollCourseService.getListCourseId(securityContextService.getCurrentUser().getEmail());
        }

        Page<CourseResponseInterface> listSubject = courseRepository.getCourseByEmail(email, enrolled,
                CommonStatus.AVAILABLE, pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapInterfacesToDtos(listSubject.getContent()))
                .totalPage(listSubject.getTotalPages())
                .totalRow(listSubject.getTotalElements())
                .build();
    }

}
