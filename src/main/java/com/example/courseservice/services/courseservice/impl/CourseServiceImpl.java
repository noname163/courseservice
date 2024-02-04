package com.example.courseservice.services.courseservice.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Level;
import com.example.courseservice.data.entities.StudentEnrolledCourses;
import com.example.courseservice.data.entities.StudentVideoProgress;
import com.example.courseservice.data.object.CourseDetailResponseInterface;
import com.example.courseservice.data.object.CourseResponseInterface;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.StudentEnrolledCoursesRepository;
import com.example.courseservice.data.repositories.StudentVideoProgressRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.EmptyException;
import com.example.courseservice.mappers.CourseMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;
import com.example.courseservice.services.levelservice.LevelService;
import com.example.courseservice.services.uploadservice.CloudinaryService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class CourseServiceImpl implements CourseService {

    private CloudinaryService uploadService;

    private CourseMapper courseMapper;

    private CourseRepository courseRepository;

    private PageableUtil pageableUtil;

    private CourseTopicService courseTopicService;

    private LevelService levelService;

    private StudentEnrolledCoursesRepository studentEnrolledCoursesRepository;

    private SecurityContextService securityContextService;

    private StudentVideoProgressRepository studentVideoProgressRepository;

    public CourseServiceImpl(CloudinaryService uploadService, CourseMapper courseMapper,
            CourseRepository courseRepository, PageableUtil pageableUtil, CourseTopicService courseTopicService,
            LevelService levelService, StudentEnrolledCoursesRepository studentEnrolledCoursesRepository,
            SecurityContextService securityContextService,
            StudentVideoProgressRepository studentVideoProgressRepository) {
        this.uploadService = uploadService;
        this.courseMapper = courseMapper;
        this.courseRepository = courseRepository;
        this.pageableUtil = pageableUtil;
        this.courseTopicService = courseTopicService;
        this.levelService = levelService;
        this.studentEnrolledCoursesRepository = studentEnrolledCoursesRepository;
        this.securityContextService = securityContextService;
        this.studentVideoProgressRepository = studentVideoProgressRepository;
    }

    private final Double MAXRATE = 5d;

    @Override
    public void createCourse(CourseRequest courseRequest, MultipartFile thumbnail) {
        Level level = levelService.getLevel(courseRequest.getLevelId());
        CloudinaryUrl thumbinial = uploadService.uploadMedia(thumbnail);
        Course course = courseMapper.mapDtoToEntity(courseRequest);
        course.setThumbnial(thumbinial.getUrl());
        course.setLevel(level);
        course.setCommonStatus(CommonStatus.DRAFT);
        course.setCloudinaryId(thumbinial.getPublicId());
        course.setCourseTopics(courseTopicService.courseTopicsByString(courseRequest.getTopic()));
        courseRepository.save(course);
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourseByEmail(String searchTerm, CommonStatus status,
            Integer page, Integer size,
            String field, SortType sortType) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<CourseResponseInterface> listSubject = courseRepository.searchCoursesForTeacher(currentUser.getId(),
                searchTerm, status.toString(), pageable);
        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapInterfacesToDtos(listSubject.getContent()))
                .totalPage(listSubject.getTotalPages())
                .totalRow(listSubject.getTotalElements())
                .build();
    }

    @Override
    public CourseDetailResponse getCourseDetail(long id, CommonStatus commonStatus) {
        if (Boolean.FALSE.equals(securityContextService.getLoginStatus())) {
            return getCourseDetailResponseForGuest(id);
        }
        UserInformation currentUser = securityContextService.getCurrentUser();
        if (currentUser.getRole().equals("STUDENT")) {
            return getCourseDetailForCurrentStudent(currentUser, id);
        } else {
            return getCourseDetailExcept(id, commonStatus);
        }
    }

    private CourseDetailResponse getCourseDetailForCurrentStudent(UserInformation student, long id) {
        StudentEnrolledCourses coursesEnrolled = studentEnrolledCoursesRepository
                .findByStudentEmailAndCourseId(student.getEmail(), id).orElse(null);
        if (coursesEnrolled == null) {
            return getCourseDetailResponseForGuest(id);
        }
        Set<Long> completedVideo = studentVideoProgressRepository
                .getCompletedVideoIdsByStudentAndCourse(student.getId(), id);
        Course course = coursesEnrolled.getCourse();
        CourseDetailResponse courseDetailResponse = courseMapper.mapCourseDetailEntityToDto(course);
        courseDetailResponse.setIsAccess(true);
        courseDetailResponse.setTotalCompleted(completedVideo.size());
        if (!completedVideo.isEmpty()) {
            int totalVideos = course.getVideos().size();
            courseDetailResponse.setProgress(totalVideos > 0 ? ((float) completedVideo.size() / totalVideos) * 100 : 0);
        }
        return courseDetailResponse;

    }

    private CourseDetailResponse getCourseDetailResponseForGuest(long id) {
        Course course = courseRepository.findByIdAndCommonStatus(id, CommonStatus.AVAILABLE)
                .orElseThrow(() -> new BadRequestException("Cannot find course with id " + id));
        CourseDetailResponse courseDetailResponse = courseMapper.mapCourseDetailEntityToDto(course);
        courseDetailResponse.setIsAccess(false);
        return courseDetailResponse;
    }

    @Override
    public Long verifyCourse(VerifyRequest verifyRequest) {
        Course updateCourse = courseRepository.findByIdAndCommonStatus(verifyRequest.getId(), CommonStatus.WAITING)
                .orElseThrow(() -> new BadRequestException("Cannot find course with status waiting"));

        Course existingCourse = updateCourse.getCourseId();

        if (existingCourse == null) {
            throw new BadRequestException("Cannot find course for update");
        }

        if (!updateCourse.getCloudinaryId().isBlank()) {
            existingCourse.setCloudinaryId(updateCourse.getCloudinaryId());
            existingCourse.setThumbnial(updateCourse.getThumbnial());
        }
        if (!updateCourse.getName().isBlank()) {
            existingCourse.setName(updateCourse.getName());
        }
        if (!updateCourse.getDescription().isBlank()) {
            existingCourse.setDescription(updateCourse.getDescription());
        }
        if (updateCourse.getPrice() != 0) {
            existingCourse.setPrice(updateCourse.getPrice());
        }
        courseRepository.save(existingCourse);
        courseRepository.delete(updateCourse);

        return existingCourse.getId();
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
        List<String> topics = courseTopicService.getTopicsByCourseId(id);
        CourseDetailResponse courseDetailResponse = courseMapper.mapToCourseDetailResponse(course);
        courseDetailResponse.setTopics(topics);
        return courseDetailResponse;

    }

    private Page<CourseResponseInterface> getCourseWhenUserLogin(String email, Integer page,
            Integer size, String field, SortType sortType) {
        List<Long> coursesId = studentEnrolledCoursesRepository.getCourseIdsByStudentEmail(email);

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        List<CommonStatus> commonStatus = List.of(CommonStatus.DELETED, CommonStatus.BANNED);
        return courseRepository.getAvailableCoursesByCommonStatusNotAndNotInList(
                commonStatus, coursesId,
                pageable);
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

        List<CourseResponse> result = courseMapper.mapInterfacesToDtos(courseResponseInterface.getContent());

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(result)
                .totalPage(courseResponseInterface.getTotalPages())
                .totalRow(courseResponseInterface.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getAllCourseForTeacher(List<CommonStatus> status, Integer page,
            Integer size, String field,
            SortType sortType) {

        String email = securityContextService.getCurrentUser().getEmail();

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<CourseResponseInterface> courseResponseInterface = courseRepository.getCourseByEmail(email, status,
                pageable);

        List<CourseResponse> courseResponse = courseMapper.mapInterfacesToDtos(courseResponseInterface.getContent());

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseResponse)
                .totalPage(courseResponseInterface.getTotalPages())
                .totalRow(courseResponseInterface.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourseByEmailForUser(String email, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        List<Long> enrolled = new ArrayList<>();
        if (Boolean.TRUE.equals(securityContextService.getLoginStatus())) {
            enrolled = studentEnrolledCoursesRepository
                    .getCourseIdsByStudentEmail(securityContextService.getCurrentUser().getEmail());
        }

        Page<CourseResponseInterface> listCourse = courseRepository.getCourseByEmailForUser(email, enrolled,
                CommonStatus.AVAILABLE, pageable);
        List<CourseResponse> result = courseMapper.mapInterfacesToDtos(listCourse.getContent());
        result = setIsAccess(result, enrolled);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(result)
                .totalPage(listCourse.getTotalPages())
                .totalRow(listCourse.getTotalElements())
                .build();
    }

    private List<CourseResponse> setIsAccess(List<CourseResponse> courseResponses, List<Long> enrolleds) {
        if (!enrolleds.isEmpty()) {
            for (CourseResponse courseResponse : courseResponses) {
                if (enrolleds.contains(courseResponse.getId())) {
                    courseResponse.setIsAccess(true);
                } else {
                    courseResponse.setIsAccess(false);
                }
            }
        }
        return courseResponses;
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourse(List<CommonStatus> commonStatus,
            Integer page, Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<CourseResponseInterface> courseResponseInterface = null;
        if (Boolean.TRUE.equals(securityContextService.getLoginStatus())) {
            UserInformation userInformation = securityContextService.getCurrentUser();
            if (userInformation.getRole().equals("ADMIN")) {
                courseResponseInterface = courseRepository.searchCoursesForAdmin(null,
                        commonStatus, pageable);
            }
            if (userInformation.getRole().equals("TEACHER")) {
                courseResponseInterface = courseRepository.getCourseByEmail(userInformation.getEmail(), commonStatus,
                        pageable);
            }
            if (securityContextService.getCurrentUser().getRole().equals("STUDENT")) {
                courseResponseInterface = getCourseWhenUserLogin(
                        securityContextService.getCurrentUser().getEmail(), page, size, field,
                        sortType);
            }
        } else {
            courseResponseInterface = courseRepository.getByCommonStatusJPQL(CommonStatus.AVAILABLE, pageable);
        }

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapInterfacesToDtos(courseResponseInterface.getContent()))
                .totalPage(courseResponseInterface.getTotalPages())
                .totalRow(courseResponseInterface.getTotalElements())
                .build();

    }

    @Override
    public void updateCourse(CourseUpdateRequest courseUpdateRequest, MultipartFile thumbinail) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Course course = courseRepository
                .findCourseByTeacherEmailAndId(currentUser.getEmail(), courseUpdateRequest.getCourseId())
                .orElseThrow(() -> new BadRequestException("Owner permission to edit content"));

        Course courseUpdate;
        if (course.getCommonStatus().equals(CommonStatus.DRAFT)) {
            courseUpdate = course;
        } else {
            courseUpdate = courseRepository.findCourseByCourseId(course)
                    .orElse(new Course());
        }
        if (thumbinail != null) {
            CloudinaryUrl cloudinaryUrl = uploadService.uploadMedia(thumbinail);
            courseUpdate.setThumbnial(cloudinaryUrl.getUrl());
            courseUpdate.setCloudinaryId(cloudinaryUrl.getPublicId());
        }
        if (courseUpdateRequest.getLevelId() != null) {
            Level level = levelService.getLevel(courseUpdateRequest.getLevelId());
            courseUpdate.setLevel(level);
        }

        courseUpdate.setCourseId(course);
        courseUpdate.setName(Optional.ofNullable(courseUpdateRequest.getName()).orElse(""));
        courseUpdate.setDescription(Optional.ofNullable(courseUpdateRequest.getDescription()).orElse(""));
        courseUpdate.setPrice(Optional.ofNullable(courseUpdateRequest.getPrice()).orElse(0.0));
        courseUpdate.setCommonStatus(CommonStatus.UPDATING);
        courseUpdate.setSubject(Optional.ofNullable(courseUpdateRequest.getSubject().getName()).orElse(""));
        courseUpdate.setSubjectId(Optional.ofNullable(courseUpdateRequest.getSubject().getId()).orElse(0l));
        courseUpdate.setTeacherEmail(currentUser.getEmail());
        courseUpdate.setTeacherId(currentUser.getId());
        courseRepository.save(courseUpdate);
    }

    @Override
    public void requestVerifyCourse(List<Long> ids) {
        Set<Long> setIds = ids.stream().distinct().collect(Collectors.toSet());
        UserInformation currentUser = securityContextService.getCurrentUser();
        List<Course> courses = courseRepository.findByIdInAndCommonStatusAndTeacherId(setIds, CommonStatus.DRAFT,
                currentUser.getId());
        if (courses == null || courses.isEmpty()) {
            throw new EmptyException("Cannot found any course with id provide");
        }
        for (Course course : courses) {
            course.setCommonStatus(CommonStatus.WAITING);
        }
        courseRepository.saveAll(courses);
    }
}
