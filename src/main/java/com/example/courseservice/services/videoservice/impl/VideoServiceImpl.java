package com.example.courseservice.services.videoservice.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.object.VideoAdminResponseInterface;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.VideoMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.videoservice.VideoHandle;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class VideoServiceImpl implements VideoService {
    private VideoRepository videoRepository;
    private CourseRepository courseRepository;
    private VideoMapper videoMapper;
    private PageableUtil pageableUtil;
    private SecurityContextService securityContextService;
    private VideoHandle videoHandle;
    

    public VideoServiceImpl(VideoRepository videoRepository, CourseRepository courseRepository, VideoMapper videoMapper,
            PageableUtil pageableUtil, SecurityContextService securityContextService, VideoHandle videoHandle) {
        this.videoRepository = videoRepository;
        this.courseRepository = courseRepository;
        this.videoMapper = videoMapper;
        this.pageableUtil = pageableUtil;
        this.securityContextService = securityContextService;
        this.videoHandle = videoHandle;
    }

    @Override
    @Transactional
    public Video saveVideoInformation(VideoRequest videoRequest) {
        Course course = courseRepository
                .findByIdAndCommonStatusNot(videoRequest.getCourseId(), CommonStatus.DELETED)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + videoRequest.getCourseId()));
        Integer maxOrdinalNumber = videoRepository.findMaxOrdinalNumberByCourse(course);

        int ordinalNumber = maxOrdinalNumber != null ? maxOrdinalNumber + 1 : 1;
        Video videoConvert = videoMapper.mapDtoToEntity(videoRequest);
        videoConvert.setStatus(CommonStatus.UNAVAILABLE);
        videoConvert.setCourse(course);
        videoConvert.setOrdinalNumber(ordinalNumber);
        return videoRepository.save(videoConvert);
    }

    @Override
    public void saveVideoFile(Video video, CloudinaryUrl videoFile, CloudinaryUrl material, CloudinaryUrl thumbnail) {
        if (video == null) {
            throw new NullPointerException("Video cannot null");
        }
        if (videoFile != null) {
            video.setCloudinaryId(videoFile.getPublicId());
            video.setDuration(videoFile.getDuration());
            video.setUrlVideo(videoFile.getUrl());
        }
        if (material != null) {
            video.setUrlMaterial(material.getUrl());
        }
        if (thumbnail != null) {
            video.setUrlThumbnail(thumbnail.getUrl());
        }
        videoRepository.save(video);
    }

    @Override
    public Video getVideoByIdAndCommonStatus(Long videoId, CommonStatus commonStatus) {
        return videoRepository
                .findByIdAndStatus(videoId, commonStatus)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + videoId));
    }

    @Override
    public VideoItemResponse getVideoDetailById(Long videoId, CommonStatus commonStatus) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        if (securityContextService.isLogin() == null) {
            return videoHandle.getVideoResponseForGuest(videoId);
        }

        if (videoHandle.isStudent() && videoHandle.canAccessVideo(videoId)) {
            return videoHandle.getVideoResponse(videoId);
        }

        if (videoHandle.isAdmin()) {
            return videoHandle.getVideoResponse(videoId);
        }

        if (videoHandle.isTeacher() && videoHandle.isTeacherVideo(currentUser.getId(), videoId)) {
            return videoHandle.getVideoResponse(videoId);
        }

        throw new BadRequestException("Not found video with id " + videoId);
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getListVideoAvailableByCourse(CommonStatus commonStatus,
            Long courseId, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Course course = videoHandle.getCourseForUser(courseId);
        if (securityContextService.isLogin() == null) {
            return videoHandle.getVideoItemResponseForGuest(course, pageable);
        } else {
            if (videoHandle.isStudent()) {
                return videoHandle.getVideoItemResponseForStudent(course, pageable);
            } else if (videoHandle.isTeacher()) {
                return getVideoForTeacher(commonStatus, page, size, field, sortType);
            } else if (videoHandle.isAdmin()) {
                return getVideoForAdmin(commonStatus, page, size, field, sortType);
            }

        }
        return null;
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getVideoForAdmin(CommonStatus commonStatus, Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<VideoAdminResponseInterface> videos = videoRepository.findAllVideosAdminResponse(commonStatus.toString(),
                pageable);

        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoMapper.mapVideosAdminResponseToVideosItem(videos.getContent()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getVideoForTeacher(CommonStatus commonStatus,
            Integer page,
            Integer size, String field, SortType sortType) {
        Long teacherId = securityContextService.getCurrentUser().getId();
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<VideoAdminResponseInterface> videos = videoRepository.findAllVideosByTeacherId(teacherId,
                commonStatus.toString(),
                pageable);
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoMapper.mapVideosAdminResponseToVideosItem(videos.getContent()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
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
        videoItemResponses = videoHandle.setIsAccess(videoItemResponses, courses);
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoItemResponses)
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
    public Video updateVideo(VideoUpdateRequest videoUpdateRequest) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Video video = videoRepository
                .findById(videoUpdateRequest.getVideoId())
                .orElseThrow(() -> new BadRequestException(
                        "Not exist video with id " + videoUpdateRequest.getVideoId() + " in function delete video"));
        Course course = video.getCourse();
        int ordinalNumber = videoRepository.findMaxOrdinalNumberByCourse(course);
        if (Boolean.FALSE.equals(
                courseRepository.existsByTeacherEmailAndId(currentUser.getEmail(), video.getCourse().getId()))) {
            throw new InValidAuthorizationException("Cannot edit this video");
        }

        Video updateVideo = videoRepository.findByVideoId(video).orElse(new Video());
        updateVideo.setName(Optional.ofNullable(videoUpdateRequest.getName()).orElse(video.getName()));
        updateVideo.setDescription(
                Optional.ofNullable(videoUpdateRequest.getDescription()).orElse(video.getDescription()));
        updateVideo.setVideoStatus(
                Optional.ofNullable(videoUpdateRequest.getVideoStatus()).orElse(video.getVideoStatus()));
        updateVideo.setUpdateTime(LocalDateTime.now());
        updateVideo.setStatus(CommonStatus.UPDATING);
        updateVideo.setVideoId(video);
        updateVideo.setCourse(course);
        updateVideo.setOrdinalNumber(++ordinalNumber);
        return videoRepository.save(updateVideo);
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
