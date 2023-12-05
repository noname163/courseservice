package com.example.courseservice.services.videoservice.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.VideoStatus;
import com.example.courseservice.data.dto.request.VideoContentUpdate;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.CourseTemporaryRepository;
import com.example.courseservice.data.repositories.StudentVideoProgressRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.VideoMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.commentservice.CommentService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.reactvideoservice.ReactVideoService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    @Lazy
    private CourseTemporaryRepository courseTemporaryRepository;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private FileService fileService;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private StudentEnrollCourseService studentEnrollCourseService;
    @Autowired
    private StudentVideoProgressRepository studentVideoProgressRepository;
    @Autowired
    private ReactVideoService reactVideoService;

    @Override
    @Transactional
    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial,
            MultipartFile material) {
        Course course = courseRepository
                .findByIdAndCommonStatusNot(videoRequest.getCourseId(), CommonStatus.DELETED)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + videoRequest.getCourseId()));
        Integer maxOrdinalNumber = videoRepository.findMaxOrdinalNumberByCourse(course);

        // Set the ordinalNumber for the new video
        int ordinalNumber = maxOrdinalNumber != null ? maxOrdinalNumber + 1 : 1;
        Video videoConvert = videoMapper.mapDtoToEntity(videoRequest);
        videoConvert.setStatus(CommonStatus.UNAVAILABLE);
        videoConvert.setCourse(course);
        videoConvert.setOrdinalNumber(ordinalNumber);
        Video videoInsert = videoRepository.save(videoConvert);
        FileResponse videoFile = fileService.fileStorage(video);
        FileResponse thumbnialFile = fileService.fileStorage(thumbnial);
        VideoResponse videoResponse = VideoResponse
                .builder()
                .videoId(videoInsert.getId())
                .video(videoFile)
                .thumbnail(thumbnialFile)
                .build();
        if (material != null) {
            FileResponse materialFile = fileService.fileStorage(material);
            videoResponse.setMaterial(materialFile);
        }
        return videoResponse;
    }

    @Override
    public void insertVideoUrl(VideoUpdate videoUpdate) {
        if (videoUpdate.getThumbnailUrl() == null
                || videoUpdate.getThumbnailUrl().equals("")
                || videoUpdate.getVideoId() < 0
                || videoUpdate.getVideoId() == null
                || videoUpdate.getVideoUrl().equals("")
                || videoUpdate.getVideoUrl() == null) {
            throw new BadRequestException("Data not valid");
        }
        Video video = getVideoByIdAndCommonStatus(videoUpdate.getVideoId(), CommonStatus.UNAVAILABLE);
        video.setUrlVideo(videoUpdate.getVideoUrl());
        video.setUrlThumbnail(videoUpdate.getThumbnailUrl());
        video.setDuration(videoUpdate.getDuration());
        video.setStatus(CommonStatus.WAITING);
        videoRepository.save(video);
    }

    @Override
    public Video getVideoByIdAndCommonStatus(Long videoId, CommonStatus commonStatus) {
        return videoRepository
                .findByIdAndStatusOrderByOrdinalNumberAsc(videoId, commonStatus)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + videoId));
    }

    @Override
    public VideoDetailResponse getAvailableVideoDetailById(Long videoId, CommonStatus commonStatus) {
        Video video = getVideoByIdAndCommonStatus(videoId, CommonStatus.AVAILABLE);
        Course course = video.getCourse();
        // Check if the video is private and user is not authenticated or not enrolled
        if (video.getVideoStatus().equals(VideoStatus.PRIVATE) && !isVideoAccessible(video)) {
            throw new InValidAuthorizationException("Buy course to view this video");
        }

        List<Video> videos = videoRepository.findByCourseAndStatusOrderByOrdinalNumberAsc(course, commonStatus);
        List<VideoItemResponse> videoItemResponses = videoMapper.mapVideosToVideoItemResponses(videos);
        // Set access status for each video item response
        videoItemResponses = setVideoAccessStatus(videoItemResponses, course.getId());

        VideoDetailResponse videoResponse = videoMapper.mapEntityToDto(video);
        videoResponse.setVideoItemResponses(videoItemResponses);
        videoResponse.setCourseId(course.getId());
        videoResponse.setDuration(video.getDuration());

        return videoResponse;
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getListVideoAvailableByCourse(Long courseId, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Course course;
        if (securityContextService.isLogin() != null) {
            course = courseRepository.findByIdAndCommonStatusNot(courseId, CommonStatus.BANNED)
                    .orElseThrow(() -> new BadRequestException("Cannot found any course with id " + courseId));
        } else {
            course = courseRepository
                    .findByIdAndCommonStatus(courseId, CommonStatus.AVAILABLE)
                    .orElseThrow(() -> new BadRequestException("Cannot found any course with id " + courseId));
        }
        Page<Video> videos = videoRepository.findByCourseAndStatusOrderByOrdinalNumberAsc(course,
                CommonStatus.AVAILABLE, pageable);
        List<VideoItemResponse> videoItemResponses = videoMapper.mapVideosToVideoItemResponses(videos.getContent());
        if (securityContextService.isLogin() != null) {
            setVideoAccessStatus(videoItemResponses, courseId);
        } else {
            for (VideoItemResponse videoItemResponse : videoItemResponses) {
                videoItemResponse.setCourseId(courseId);
                if (videoItemResponse.getVideoStatus().equals(VideoStatus.PRIVATE)) {
                    videoItemResponse.setIsAccess(false);
                } else {
                    videoItemResponse.setIsAccess(true);
                }
            }
        }
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoItemResponses)
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();

    }

    @Override
    public PaginationResponse<List<VideoAdminResponse>> getVideoForAdmin(CommonStatus commonStatus, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        if (CommonStatus.ALL.equals(commonStatus)) {
            Page<Video> videos = videoRepository.findAll(pageable);
            return PaginationResponse.<List<VideoAdminResponse>>builder()
                    .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                    .totalPage(videos.getTotalPages())
                    .totalRow(videos.getTotalElements())
                    .build();
        }
        Page<Video> videos = videoRepository.findByStatusOrderByOrdinalNumberAsc(commonStatus, pageable);
        return PaginationResponse.<List<VideoAdminResponse>>builder()
                .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<VideoAdminResponse>> getVideoForTeacher(CommonStatus commonStatus,
            Integer page,
            Integer size, String field, SortType sortType) {
        String email = securityContextService.getCurrentUser().getEmail();
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        List<Course> courses = courseRepository.findCourseByTeacherEmail(email);
        if (courses.isEmpty()) {
            return null;
        }
        if (CommonStatus.ALL.equals(commonStatus)) {
            Page<Video> videos = videoRepository.findByCourseInAndStatusNotOrderByOrdinalNumberAsc(courses,
                    CommonStatus.DELETED, pageable);
            return PaginationResponse.<List<VideoAdminResponse>>builder()
                    .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                    .totalPage(videos.getTotalPages())
                    .totalRow(videos.getTotalElements())
                    .build();
        }
        Page<Video> videos = videoRepository.findByStatusAndCourseInOrderByOrdinalNumberAsc(commonStatus, courses,
                pageable);
        return PaginationResponse.<List<VideoAdminResponse>>builder()
                .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

    @Override
    public VideoDetailResponse getVideoDetailByIdExcept(Long videoId, CommonStatus commonStatus) {
        Video video = videoRepository.findByIdAndStatusNotOrderByOrdinalNumberAsc(videoId, commonStatus)
                .orElseThrow(() -> new BadRequestException("Not exist video with id: " + videoId));

        List<Video> videos = videoRepository.findByCourseAndStatusNotOrderByOrdinalNumberAsc(video.getCourse(),
                commonStatus);

        List<VideoItemResponse> videoItemResponses = new ArrayList<>();
        if (videos != null && !videos.isEmpty()) {
            videoItemResponses = videoMapper.mapVideosToVideoItemResponses(videos);
        }
        VideoDetailResponse videoDetailResponse = videoMapper.mapEntityToDto(video);
        videoDetailResponse.setReactStatus(reactVideoService.getReactStatusByStudentIdAndVideoId(videoId));
        videoDetailResponse.setVideoItemResponses(videoItemResponses);
        return videoDetailResponse;
    }

    @Override
    public void updateVideoOrder(List<VideoOrder> videoOrders, Long courseId) {

        Set<Long> videoIds = videoOrders.stream()
                .map(VideoOrder::getVideoId)
                .collect(Collectors.toSet());

        List<Video> videosToUpdate = videoRepository.findByCourseIdAndIdInOrderByOrdinalNumberAsc(courseId, videoIds);

        Map<Long, Video> videoMap = videosToUpdate.stream()
                .collect(Collectors.toMap(Video::getId, Function.identity()));

        List<Video> updatedVideos = videoOrders.stream()
                .filter(videoOrder -> videoMap.containsKey(videoOrder.getVideoId()))
                .map(videoOrder -> {
                    Video video = videoMap.get(videoOrder.getVideoId());
                    video.setOrdinalNumber(videoOrder.getVideoOrder());
                    return video;
                })
                .collect(Collectors.toList());

        videoRepository.saveAll(updatedVideos);
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getVideoForUser(String email,
            Integer page, Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        List<Course> courses = courseRepository.findCourseByTeacherEmail(email);
        if (courses.isEmpty()) {
            return null;
        }
        Page<Video> videos = videoRepository.findByStatusAndCourseInOrderByOrdinalNumberAsc(CommonStatus.AVAILABLE,
                courses, pageable);
        List<VideoItemResponse> videoItemResponses = videoMapper.mapVideosToVideoItemResponses(videos.getContent());
        videoItemResponses = setIsAccess(videoItemResponses, courses);
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoItemResponses)
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

    @Override
    public Video getVideoByIdAndCommonStatusNot(Long videoId, CommonStatus commonStatus) {
        Optional<Video> video = videoRepository.findByIdAndStatusNotOrderByOrdinalNumberAsc(videoId, commonStatus);
        if (video.isEmpty()) {
            throw new BadRequestException(
                    "Cannot found video with id " + videoId + " in function getVideoByIdAndCommonStatusNot");
        }
        return video.get();
    }

    @Override
    @Transactional
    public VideoResponse uploadVideoByCourse(VideoRequest videoRequest, MultipartFile video,
            MultipartFile thumbnail) {
        UserInformation currentUser = securityContextService.getCurrentUser();

        Course course = courseRepository
                .findByIdAndCommonStatusNot(videoRequest.getCourseId(), CommonStatus.DELETED)
                .orElseThrow(() -> new BadRequestException("Not exist course with id "
                        + videoRequest.getCourseId() + " in function uploadVideoByCourse"));

        if (!course.getTeacherEmail().equals(currentUser.getEmail())) {
            throw new InValidAuthorizationException("Cannot edit this video");
        }
        Video videoConvert = videoMapper.mapDtoToEntity(videoRequest);
        videoConvert.setStatus(CommonStatus.WAITING);
        videoConvert.setCourse(course);
        Video videoInsert = videoRepository.save(videoConvert);
        FileResponse videoFile = fileService.fileStorage(video);
        FileResponse thumbnialFile = fileService.fileStorage(thumbnail);

        course.setCommonStatus(CommonStatus.UPDATING);
        courseRepository.save(course);

        return VideoResponse
                .builder()
                .videoId(videoInsert.getId())
                .video(videoFile)
                .thumbnail(thumbnialFile)
                .build();
    }

    private boolean isVideoAccessible(Video video) {
        boolean isAuthenticatedStudent = securityContextService.getIsAuthenticatedAndIsStudent();
        boolean isVideoPrivate = video.getVideoStatus().equals(VideoStatus.PRIVATE);
        if ((!isAuthenticatedStudent && isVideoPrivate) ||
                (isAuthenticatedStudent && isVideoPrivate && !isStudentEnrolled(video.getCourse().getId()))) {
            return false;
        }

        return true;
    }

    private boolean isStudentEnrolled(Long courseId) {
        String studentEmail = securityContextService.getCurrentUser().getEmail();
        return studentEnrollCourseService.isStudentEnrolled(studentEmail, courseId);
    }

    private List<VideoItemResponse> setVideoAccessStatus(
            List<VideoItemResponse> videoItemResponses, Long courseId) {
        boolean isUserLoggedIn = false;
        if (securityContextService.isLogin() != null && securityContextService.isLogin().getRole().equals("STUDENT")) {
            isUserLoggedIn = true;
        }
        List<VideoItemResponse> result = new ArrayList<>();
        for (VideoItemResponse videoItemResponse : videoItemResponses) {
            boolean isVideoPrivate = videoItemResponse.getVideoStatus().equals(VideoStatus.PRIVATE);
            if (!isUserLoggedIn && isVideoPrivate) {
                videoItemResponse.setIsAccess(false);
            }
            if (!isVideoPrivate) {
                videoItemResponse.setIsAccess(true);
            }

            if (isUserLoggedIn) {
                boolean isEnrolled = isStudentEnrolled(courseId);
                videoItemResponse.setIsAccess(isEnrolled || !isVideoPrivate);
                Set<Long> isWatched = studentVideoProgressRepository
                        .getCompletedVideoIdsByStudentAndCourse(securityContextService.getCurrentUser().getId(),
                                courseId);
                if (!isWatched.isEmpty() && isWatched.contains(videoItemResponse.getId())) {
                    videoItemResponse.setIsWatched(true);
                }
            }
            videoItemResponse.setCourseId(courseId);
            result.add(videoItemResponse);
        }
        return result;
    }

    private List<VideoItemResponse> setIsAccess(List<VideoItemResponse> videoItemResponses, List<Course> courses) {
        UserInformation currentUser = securityContextService.isLogin();
        List<VideoItemResponse> result = new ArrayList<>();

        if (currentUser != null) {
            List<Long> videoAccess = studentEnrollCourseService.getListVideoIdStudentAccess(currentUser.getEmail(),
                    courses);

            for (VideoItemResponse videoItemResponse : videoItemResponses) {
                if (videoAccess.isEmpty()) {
                    // If videoAccess is empty, set isAccess based on video status
                    videoItemResponse.setIsAccess(!videoItemResponse.getVideoStatus().equals(VideoStatus.PRIVATE));
                } else {
                    // If videoAccess is not empty, set isAccess to true when video ID is in
                    // videoAccess
                    videoItemResponse.setIsAccess(videoAccess.contains(videoItemResponse.getId()));
                }

                result.add(videoItemResponse);
            }
        } else {
            for (VideoItemResponse videoItemResponse : videoItemResponses) {
                videoItemResponse.setIsAccess(videoItemResponse.getVideoStatus().equals(VideoStatus.PUBLIC));
                result.add(videoItemResponse);
            }
        }

        return result;
    }

    @Override
    public void deleteVideo(Long videoId) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Video video = videoRepository
                .findById(videoId)
                .orElseThrow(() -> new BadRequestException(
                        "Not exist video with id " + videoId + " in function delete video"));
        Course course = video.getCourse();
        if (Boolean.FALSE.equals(course.getTeacherEmail().equals(currentUser.getEmail()))) {
            throw new InValidAuthorizationException("Cannot delete this video");
        }
        video.setStatus(CommonStatus.DELETED);
        videoRepository.save(video);
    }

    @Override
    public List<CourseVideoResponse> getVideoByCourseIdAndCommonStatus(Long courseId, CommonStatus commonStatus) {
        if (commonStatus.equals(CommonStatus.ALL)) {
            return videoMapper.mapToCourseVideoResponseList(
                    videoRepository.getCourseVideosByCourseIdAndCommonStatusNot(courseId, CommonStatus.DELETED));
        }
        return videoMapper.mapToCourseVideoResponseList(
                videoRepository.getCourseVideosByCourseIdAndCommonStatus(courseId, CommonStatus.AVAILABLE));
    }

    @Override
    public void editVideoContent(VideoContentUpdate videoUpdateRequest) {

        UserInformation currentUser = securityContextService.getCurrentUser();
        Video video = videoRepository
                .findById(videoUpdateRequest.getVideoId())
                .orElseThrow(() -> new BadRequestException(
                        "Not exist video with id " + videoUpdateRequest.getVideoId() + " in function delete video"));

        if (Boolean.FALSE.equals(
                courseRepository.existsByTeacherEmailAndId(currentUser.getEmail(), video.getCourse().getId()))) {
            throw new InValidAuthorizationException("Cannot edit this video");
        }

        video.setName(Optional.ofNullable(videoUpdateRequest.getName()).orElse(video.getName()));
        video.setDescription(Optional.ofNullable(videoUpdateRequest.getDescription()).orElse(video.getDescription()));
        video.setVideoStatus(Optional.ofNullable(videoUpdateRequest.getVideoStatus()).orElse(video.getVideoStatus()));
        video.setUpdateTime(LocalDateTime.now());
        videoRepository.save(video);
    }

    @Override
    public PaginationResponse<List<VideoAdminResponse>> getVideoByCourseId(Long courseId, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<Video> videos = videoRepository.findByCourseIdOrderByOrdinalNumberAsc(courseId, pageable);
        return PaginationResponse.<List<VideoAdminResponse>>builder()
                .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

}
