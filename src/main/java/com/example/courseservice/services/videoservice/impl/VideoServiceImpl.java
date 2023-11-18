package com.example.courseservice.services.videoservice.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.VerifyStatus;
import com.example.courseservice.data.constants.VideoStatus;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Comment;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.entities.VideoTemporary;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.object.VideoItemResponseInterface;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.data.repositories.CommentRepository;
import com.example.courseservice.data.repositories.CourseRepository;
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
import com.example.courseservice.services.videotmpservice.VideoTmpService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideoTmpService videoTmpService;
    @Autowired
    private CourseRepository courseRepository;
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
    private CommentService commentService;
    @Autowired
    private ReactVideoService reactVideoService;

    @Override
    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial) {
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
        return VideoResponse
                .builder()
                .videoId(videoInsert.getId())
                .video(videoFile)
                .thumbnail(thumbnialFile)
                .build();
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
                .findByIdAndStatus(videoId, commonStatus)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + videoId));
    }

    @Override
    public VideoDetailResponse getAvailableVideoDetailById(Long videoId, CommonStatus commonStatus) {
        Video video = getVideoByIdAndCommonStatus(videoId, CommonStatus.AVAILABLE);
        Course course = video.getCourse();
        // Check if the video is private and user is not authenticated or not enrolled
        if (!isVideoAccessible(video)) {
            throw new InValidAuthorizationException("Buy course to view this video");
        }

        List<Video> videos = videoRepository.findByCourseAndStatus(course, commonStatus);
        List<VideoItemResponse> videoItemResponses = videoMapper.mapVideosToVideoItemResponses(videos);

        // Set access status for each video item response
        videoItemResponses = setVideoAccessStatus(videoItemResponses, course.getId());

        VideoDetailResponse videoResponse = videoMapper.mapEntityToDto(video);
        videoResponse.setVideoItemResponses(videoItemResponses);

        return videoResponse;
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getListVideoAvailableByCourse(Long courseId, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Course course = courseRepository
                .findByIdAndCommonStatus(courseId, CommonStatus.AVAILABLE)
                .orElseThrow(() -> new BadRequestException("Cannot found any course with id " + courseId));

        Page<Video> videos = videoRepository.findByCourseAndStatus(course, CommonStatus.AVAILABLE, pageable);
        List<VideoItemResponse> videoItemResponses = videoMapper.mapVideosToVideoItemResponses(videos.getContent());
        if (securityContextService.isLogin() != null) {
            setVideoAccessStatus(videoItemResponses, courseId);
        } else {
            for (VideoItemResponse videoItemResponse : videoItemResponses) {
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
        Page<Video> videos = videoRepository.findByStatus(commonStatus, pageable);
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
            Page<Video> videos = videoRepository.findByCourseIn(courses, pageable);
            return PaginationResponse.<List<VideoAdminResponse>>builder()
                    .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                    .totalPage(videos.getTotalPages())
                    .totalRow(videos.getTotalElements())
                    .build();
        }
        Page<Video> videos = videoRepository.findByStatusAndCourseIn(commonStatus, courses, pageable);
        return PaginationResponse.<List<VideoAdminResponse>>builder()
                .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

    @Override
    public void verifyVideo(VerifyRequest verifyRequest) {
        Video video = videoRepository.findById(verifyRequest.getId())
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + verifyRequest.getId()));
        if (!videoTmpService.isUpdate(verifyRequest.getId())) {
            if (video.getStatus() == CommonStatus.AVAILABLE
                    && verifyRequest.getVerifyStatus().equals(VerifyStatus.ACCEPTED)) {
                throw new BadRequestException("There is no difference to change");
            }
            if (video.getStatus() == CommonStatus.REJECT
                    && verifyRequest.getVerifyStatus().equals(VerifyStatus.REJECT)) {
                throw new BadRequestException("There is no difference to change");
            }
            if (VerifyStatus.ACCEPTED.equals(verifyRequest.getVerifyStatus())) {
                video.setStatus(CommonStatus.AVAILABLE);
            } else {
                video.setStatus(CommonStatus.REJECT);
            }
            videoRepository.save(video);
        } else {
            if (VerifyStatus.ACCEPTED.equals(verifyRequest.getVerifyStatus())) {
                videoTmpService.insertVideoTmpToReal(verifyRequest.getId());
            }
        }
    }

    @Override
    public VideoDetailResponse getVideoDetailByIdExcept(Long videoId, CommonStatus commonStatus) {
        Video video = videoRepository.findByIdAndStatusNot(videoId, commonStatus)
                .orElseThrow(() -> new BadRequestException("Not exist video with id: " + videoId));

        List<Video> videos = videoRepository.findByCourseAndStatusNot(video.getCourse(), commonStatus);

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

        List<Video> videosToUpdate = videoRepository.findByCourseIdAndIdIn(courseId, videoIds);

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
    public PaginationResponse<List<VideoAdminResponse>> getVideoForUser(String email,
            Integer page, Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        List<Course> courses = courseRepository.findCourseByTeacherEmail(email);
        if (courses.isEmpty()) {
            return null;
        }
        Page<Video> videos = videoRepository.findByStatusAndCourseIn(CommonStatus.AVAILABLE, courses, pageable);
        return PaginationResponse.<List<VideoAdminResponse>>builder()
                .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

    @Override
    public Video getVideoByIdAndCommonStatusNot(Long videoId, CommonStatus commonStatus) {
        Optional<Video> video = videoRepository.findByIdAndStatusNot(videoId, commonStatus);
        if (video.isEmpty()) {
            throw new BadRequestException(
                    "Cannot found video with id " + videoId + " in function getVideoByIdAndCommonStatusNot");
        }
        return video.get();
    }

    @Override
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

    private List<VideoItemResponse> setVideoAccessStatus(List<VideoItemResponse> videoItemResponses, Long courseId) {
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

            if (isUserLoggedIn) {
                boolean isEnrolled = isStudentEnrolled(courseId);
                videoItemResponse.setIsAccess(isEnrolled || !isVideoPrivate);
            }
            result.add(videoItemResponse);
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
        if(Boolean.FALSE.equals(courseRepository.existsByTeacherEmailAndId(currentUser.getEmail(), video.getId()))){
            throw new InValidAuthorizationException("Cannot delete this video");
        }
        commentService.deleteComments(video);
        videoRepository.delete(video);
    }

    @Override
    public List<CourseVideoResponse> getVideoByCourseIdAndCommonStatus(Long courseId, CommonStatus commonStatus) {
        if(commonStatus.equals(CommonStatus.ALL)){
            return videoMapper.mapToCourseVideoResponseList(videoRepository.getCourseVideosByCourseIdAndCommonStatusNot(courseId, CommonStatus.DELETED));
        }
        return videoMapper.mapToCourseVideoResponseList(videoRepository.getCourseVideosByCourseIdAndCommonStatus(courseId, CommonStatus.DELETED));
    }

}
