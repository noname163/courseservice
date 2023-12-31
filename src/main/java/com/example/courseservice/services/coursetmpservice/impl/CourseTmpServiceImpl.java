package com.example.courseservice.services.coursetmpservice.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.NotificationTitle;
import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.CourseTemporaryUpdateRequest;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.request.SendMailRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.Level;
import com.example.courseservice.data.object.CourseResponseInterface;
import com.example.courseservice.data.object.NotificationContent;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.CourseTemporaryRepository;
import com.example.courseservice.data.repositories.LevelRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.CourseTemporaryMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetmpservice.CourseTmpService;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.notificationservice.NotificationService;
import com.example.courseservice.services.sendmailservice.SendEmailService;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.services.videotmpservice.VideoTmpService;
import com.example.courseservice.template.SendMailTemplate;
import com.example.courseservice.utils.PageableUtil;

@Service
public class CourseTmpServiceImpl implements CourseTmpService {
    @Autowired
    private CourseTemporaryRepository courseTemporaryRepository;
    @Autowired
    private CourseRepository courseRepository;
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
    @Autowired
    @Lazy
    private SendEmailService sendEmailService;
    @Autowired
    @Lazy
    private NotificationService notificationService;

    @Override
    @Transactional
    public void createCourse(CourseRequest courseRequest, MultipartFile thumbnail) {
        FileResponse fileResponse = fileService.fileStorage(thumbnail);
        CloudinaryUrl thumbinial = uploadService.uploadMedia(fileResponse);
        CourseTemporary course = courseTemporaryMapper.mapDtoToEntity(courseRequest);
        course.setThumbnial(thumbinial.getUrl());
        course.setLevelId(courseRequest.getLevelId());
        course.setStatus(CommonStatus.DRAFT);
        course.setCreatedDate(LocalDateTime.now());
        course = courseTemporaryRepository.save(course);
        courseTopicService.createCourseTopics(courseRequest.getTopic(), course);
    }

    @Override
    @Transactional
    public void updateRealCourse(CourseUpdateRequest courseUpdateRequest, MultipartFile thumbnail) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Course course = courseService.getCourseByIdAndEmail(courseUpdateRequest.getCourseId(),
                currentUser.getEmail());
        CloudinaryUrl thumbnailUrl = processThumbnail(thumbnail);
        Optional<CourseTemporary> existingCourseTemporary = courseTemporaryRepository
                .findByCourseId(courseUpdateRequest.getCourseId());

        CourseTemporary courseTemporary = createOrUpdateCourseTemporary(existingCourseTemporary.orElse(null),
                courseUpdateRequest, course, thumbnailUrl, currentUser);

        courseTopicService.addCourseTemporaryToTopic(course, courseTemporary);

        updateVideoOrders(courseUpdateRequest.getVideoOrders(), course.getId(), courseTemporary.getId());
    }

    private CloudinaryUrl processThumbnail(MultipartFile thumbnail) {
        return (thumbnail != null) ? uploadService.uploadMedia(fileService.fileStorage(thumbnail)) : null;
    }

    private CourseTemporary createOrUpdateCourseTemporary(CourseTemporary existingCourseTemporary,
            CourseUpdateRequest courseUpdateRequest, Course course,
            CloudinaryUrl thumbnailUrl, UserInformation currentUser) {
        CourseTemporary courseTemporary = (existingCourseTemporary != null)
                ? courseTemporaryMapper.mapCourseTemporary(existingCourseTemporary, courseUpdateRequest,
                        course)
                : courseTemporaryMapper.mapDtoToEntity(courseUpdateRequest, course);

        courseTemporary.setThumbnial(thumbnailUrl != null ? thumbnailUrl.getUrl() : course.getThumbnial());
        courseTemporary.setStatus(CommonStatus.UPDATING);
        courseTemporary.setTeacherEmail(currentUser.getEmail());
        courseTemporary.setCreatedDate(LocalDateTime.now());
        courseTemporary.setTeacherAvatar(course.getTeacherAvatar());
        courseTemporary.setTeacherId(currentUser.getId());
        courseTemporary.setSubject(course.getSubject());
        courseTemporary.setTeacherName(currentUser.getFullname());

        return courseTemporaryRepository.save(courseTemporary);
    }

    @Override
    public void updateVideoOrders(List<VideoOrder> videoOrders, Long courseId, Long courseTemporaryId) {
        if (videoOrders != null && !videoOrders.isEmpty()) {
            List<VideoOrder> videoOrdersTemporary = videoOrders.stream()
                    .filter(VideoOrder::getIsDraft)
                    .collect(Collectors.toList());

            List<VideoOrder> videoOrdersReal = videoOrders.stream()
                    .filter(videoOrder -> !videoOrder.getIsDraft())
                    .collect(Collectors.toList());

            if (courseId != null) {
                videoService.updateVideoOrder(videoOrdersReal, courseId);
            }
            if (courseTemporaryId != null) {
                videoTmpService.updateVideoOrder(videoOrdersTemporary, courseTemporaryId);
            }
        }
    }

    @Override
    public void editTmpCourse(CourseTemporaryUpdateRequest courseUpdateRequest, MultipartFile thumbnail) {

        UserInformation currentUser = securityContextService.getCurrentUser();
        CourseTemporary courseTemporary = courseTemporaryRepository
                .findByTeacherIdAndId(currentUser.getId(), courseUpdateRequest.getCourseId())
                .orElseThrow(() -> new InValidAuthorizationException(
                        "Require permission to edit this course"));
        if (courseTemporary.getStatus().equals(CommonStatus.WAITING)
                && courseTemporary.getStatus().equals(CommonStatus.BANNED)) {
            throw new BadRequestException("Cannot edit course temporary in this status");
        }
        CloudinaryUrl thumbnial = null;
        if (thumbnail != null) {
            FileResponse fileResponse = fileService.fileStorage(thumbnail);
            thumbnial = uploadService.uploadMedia(fileResponse);
        }

        courseTemporary.setThumbnial(thumbnial != null ? thumbnial.getUrl() : courseTemporary.getThumbnial());
        courseTemporary = courseTemporaryMapper.mapUpdateTemporaryToCourseTemporary(courseUpdateRequest,
                courseTemporary);
        courseTemporary.setStatus(CommonStatus.DRAFT);
        courseTemporary = courseTemporaryRepository.save(courseTemporary);
        Course course = courseTemporary.getCourse();
        if (course != null) {
            updateVideoOrders(courseUpdateRequest.getVideoOrders(), course.getId(),
                    courseTemporary.getId());
        } else {
            if (courseUpdateRequest.getVideoOrders() != null) {
                videoTmpService.updateVideoOrder(courseUpdateRequest.getVideoOrders(),
                        courseTemporary.getId());
            }
        }

    }

    @Override
    public PaginationResponse<List<CourseResponse>> getCourseTmpAndStatusNot(String searchTerm, CommonStatus status,
            Integer page,
            Integer size, String field,
            SortType sortType) {

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<CourseResponseInterface> courseTemporary = courseTemporaryRepository
                .searchByNameForAdmin(searchTerm, status, pageable);

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
    @Transactional
    public void insertUpdateCourseTmpToReal(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        CourseTemporary courseTemporary = courseTemporaryRepository
                .findByCourseId(courseId)
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found course update with id " + courseId));
        course = courseTemporaryMapper.mapCoursetmpToCourse(course, courseTemporary);
        courseRepository.save(course);
        courseTemporaryRepository.delete(courseTemporary);
    }

    @Override
    public CourseDetailResponse getCourseDetail(Long id) {
        CourseTemporary courseTemporary = courseTemporaryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "Not found course temporary with id " + id
                                + " in getCourseDetail temporary function"));
        Level level = levelRepository.findById(courseTemporary.getLevelId())
                .orElseThrow(
                        () -> new BadRequestException("Cannot found level with id "
                                + courseTemporary.getLevelId()
                                + " in function insertCourseTmpToReal"));

        List<CourseVideoResponse> courseVideoTemporaryResponse = videoTmpService.getCourseVideoResponseById(id);
        Course course = courseTemporary.getCourse();
        CourseDetailResponse courseDetailResponse = courseTemporaryMapper
                .mapCourseDetailResponse(courseTemporary);
        List<String> topics;
        if (course != null) {
            Long courseId = course.getId();
            List<CourseVideoResponse> courseVideoResponse = videoService.getVideoByCourseIdAndCommonStatus(
                    courseId,
                    CommonStatus.AVAILABLE);
            courseDetailResponse.setCourseRealId(courseId);
            courseVideoTemporaryResponse.addAll(courseVideoResponse);
            topics = courseTopicService.getTopicsByCourseId(courseId);
        } else {
            topics = courseTopicService.getTopicsByCourseTmpId(id);
        }
        courseDetailResponse.setTopics(topics);
        courseDetailResponse.setLevel(level.getName());
        courseDetailResponse.setCourseVideoResponses(courseVideoTemporaryResponse);

        return courseDetailResponse;
    }

    @Override
    @Transactional
    public Long insertCourseTmpToReal(VerifyRequest verifyRequest) {
        CourseTemporary courseTemporary = courseTemporaryRepository.findById(verifyRequest.getId())
                .orElseThrow(
                        () -> new BadRequestException("Cannot found course temporary with id "
                                + verifyRequest.getId()
                                + " in function insertCourseTmpToReal"));
        Course course = null;
        if (courseTemporary.getCourse() == null) {
            Level level = levelRepository.findById(courseTemporary.getLevelId())
                    .orElseThrow(
                            () -> new BadRequestException("Cannot found level with id "
                                    + courseTemporary.getLevelId()
                                    + " in function insertCourseTmpToReal"));

            course = courseTemporaryMapper.mapToCourse(courseTemporary);
            course.setCommonStatus(CommonStatus.AVAILABLE);
            course.setLevel(level);
            course.setTeacherId(courseTemporary.getTeacherId());
            course.setTeacherAvatar(courseTemporary.getTeacherAvatar());
            course = courseRepository.save(course);
            courseTopicService.updateCourseTopicByCourseTemporary(courseTemporary, course);
        } else {
            course = courseTemporary.getCourse();
            course.setCommonStatus(CommonStatus.AVAILABLE);
            course.setDescription(
                    Optional.ofNullable(courseTemporary.getDescription())
                            .orElse(course.getDescription()));
            course.setName(Optional.ofNullable(courseTemporary.getName()).orElse(course.getName()));
            course.setPrice(Optional.ofNullable(courseTemporary.getPrice()).orElse(course.getPrice()));
            course.setUpdateTime(LocalDateTime.now());
            course.setThumbnial(Optional.ofNullable(courseTemporary.getThumbnial()).orElse(course.getThumbnial()));
            course.setTeacherId(courseTemporary.getTeacherId());
            course = courseRepository.save(course);
            courseTopicService.updateCourseTopicByCourseTemporary(courseTemporary, course);
        }
        videoTmpService.insertVideoTmpToReal(verifyRequest.getId(), course);
        courseTemporaryRepository.delete(courseTemporary);
        String mailTemplate = SendMailTemplate.approveEmail(courseTemporary.getTeacherName(),
                courseTemporary.getName());

        sendEmailService.sendMailService(SendMailRequest
                .builder()
                .mailTemplate(mailTemplate)
                .subject(NotificationTitle.VERIFY_TITLE)
                .userEmail(courseTemporary.getTeacherEmail())
                .build());
        notificationService.createNotification(NotificationContent
                .builder()
                .content(mailTemplate)
                .title(NotificationTitle.VERIFY_TITLE)
                .type(NotificationType.SYSTEM)
                .email(courseTemporary.getTeacherEmail())
                .userId(courseTemporary.getTeacherId())
                .build());
        return course.getId();
    }

    @Override
    public void rejectCourse(VerifyRequest actionRequest) {
        CourseTemporary courseTemporary = courseTemporaryRepository.findById(actionRequest.getId())
                .orElseThrow(
                        () -> new BadRequestException("Cannot found course temporary with id "
                                + actionRequest.getId()
                                + " in function rejectCourse"));
        if (courseTemporary.getStatus().equals(CommonStatus.REJECT)) {
            throw new BadRequestException(
                    "Course with id " + actionRequest.getId() + " have been rejected.");
        }
        courseTemporary.setStatus(CommonStatus.REJECT);
        courseTemporaryRepository.save(courseTemporary);

        String mailTemplate = SendMailTemplate.rejectEmail(courseTemporary.getTeacherName(),
                courseTemporary.getName(),
                actionRequest.getReason());
        sendEmailService.sendMailService(SendMailRequest
                .builder()
                .mailTemplate(mailTemplate)
                .subject(NotificationTitle.VERIFY_TITLE)
                .userEmail(courseTemporary.getTeacherEmail())
                .build());
        notificationService.createNotification(NotificationContent
                .builder()
                .content(mailTemplate)
                .title(NotificationTitle.VERIFY_TITLE)
                .type(NotificationType.SYSTEM)
                .email(courseTemporary.getTeacherEmail())
                .userId(courseTemporary.getTeacherId())
                .build());
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getCourseTmpByEmailAndStatusNot(String searchTerm,
            CommonStatus status,
            Integer page, Integer size, String field, SortType sortType) {
        String email = securityContextService.getCurrentUser().getEmail();
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<CourseResponseInterface> courseTemporary = courseTemporaryRepository
                .searchByNameForTeacher(searchTerm, email, status.toString(), pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseTemporaryMapper.mapInterfacesToDtos(courseTemporary.getContent()))
                .totalPage(courseTemporary.getTotalPages())
                .totalRow(courseTemporary.getTotalElements())
                .build();
    }

    @Override
    public void requestVerifyCourses(List<Long> courseIds) {
        Long teacherId = securityContextService.getCurrentUser().getId();
        List<CourseTemporary> courseTemporaries = courseTemporaryRepository.findByTeacherIdAndIdIn(teacherId,
                courseIds);
        if (courseTemporaries.isEmpty()) {
            throw new BadRequestException("Invaild data in function requestVerifyCourses");
        }
        List<CourseTemporary> courseTemporariesUpdate = new ArrayList<>();
        for (CourseTemporary courseTemporary : courseTemporaries) {
            if (courseTemporary.getStatus().equals(CommonStatus.REJECT)) {
                throw new BadRequestException("Edit course before request verify.");
            }
            if (courseTemporary.getVideoTemporaries().isEmpty() && courseTemporary.getCourse() == null
                    && !courseTemporary.getStatus().equals(CommonStatus.UPDATING)) {
                throw new BadRequestException("Course must have video to verify.");
            }
            if (courseTemporary.getCourseTopics().isEmpty()) {
                throw new BadRequestException("Course must have topic to verify.");
            }
            courseTemporary.setStatus(CommonStatus.WAITING);
            courseTemporariesUpdate.add(courseTemporary);
        }
        courseTemporaryRepository.saveAll(courseTemporariesUpdate);
    }

    @Override
    public PaginationResponse<List<CourseResponse>> searchTemporaryCourseForTeacher(String searchTerm, Integer page,
            Integer size,
            String field, SortType sortType) {
        Long teacherId = securityContextService.getCurrentUser().getId();
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<CourseTemporary> courseTemporaries = courseTemporaryRepository.searchCourseTemporariesByTeacher(
                searchTerm,
                teacherId,
                pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseTemporaryMapper
                        .mapCoursesTmpToCourseResponses(courseTemporaries.getContent()))
                .totalPage(courseTemporaries.getTotalPages())
                .totalRow(courseTemporaries.getTotalElements())
                .build();
    }

    @Override
    public CourseTemporary createNewCourseTemporaryByCourse(Course course) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        CourseTemporary courseTemporary = courseTemporaryRepository.findByCourse(course);
        if (courseTemporary == null) {
            courseTemporary = courseTemporaryRepository.save(CourseTemporary
                    .builder()
                    .updateTime(LocalDateTime.now())
                    .status(CommonStatus.UPDATING)
                    .course(course)
                    .name(course.getName())
                    .price(course.getPrice())
                    .description(course.getDescription())
                    .teacherEmail(course.getTeacherEmail())
                    .teacherName(currentUser.getFullname())
                    .teacherId(currentUser.getId())
                    .thumbnial(course.getThumbnial())
                    .subject(course.getSubject())
                    .levelId(course.getLevel().getId())
                    .build());
        }
        courseTopicService.addCourseTemporaryToTopic(course, courseTemporary);
        return courseTemporary;
    }

    @Override
    public void deleteDraftCourse(Long id) {
        CourseTemporary courseTemporary = courseTemporaryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Not exist course draft with id " + id));
        Long teacherId = securityContextService.getCurrentUser().getId();
        if (courseTemporary.getTeacherId() != teacherId) {
            throw new InValidAuthorizationException("Owner permission require");
        }
        videoTmpService.deletedTemporaryVideoByCourseTmpId(id);
        courseTopicService.removeTopicByCourseTmp(courseTemporary);
        courseTemporaryRepository.delete(courseTemporary);
    }

    @Override
    public PaginationResponse<List<CourseResponse>> filterCourseTmpStatus(CommonStatus status, Integer page,
            Integer size, String field, SortType sortType) {
        UserInformation user = securityContextService.getCurrentUser();
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<CourseResponseInterface> courseTemporary;
        if (user.getRole().equals("TEACHER")) {
            courseTemporary = courseTemporaryRepository.filterByEmailAndStatus(user.getEmail(), status,
                    pageable);
        } else {
            courseTemporary = courseTemporaryRepository.filterByStatus(status, pageable);
        }

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseTemporaryMapper.mapInterfacesToDtos(courseTemporary.getContent()))
                .totalPage(courseTemporary.getTotalPages())
                .totalRow(courseTemporary.getTotalElements())
                .build();
    }

}
