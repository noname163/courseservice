package com.example.courseservice.services.videoservice.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.VideoMapper;
import com.example.courseservice.services.fileservice.FileService;
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

    @Override
    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial) {
        Course course = courseRepository
                .findByIdAndCommonStatusNot(videoRequest.getCourseId(), CommonStatus.DELETED)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + videoRequest.getCourseId()));
        Integer maxOrdinalNumber = videoRepository.findMaxOrdinalNumberByCourse(course);

        // Set the ordinalNumber for the new video
        int ordinalNumber = maxOrdinalNumber != null ? maxOrdinalNumber + 1 : 1;
        Video videoConvert = videoMapper.mapDtoToEntity(videoRequest);
        videoConvert.setStatus(CommonStatus.WAITING);
        videoConvert.setCourse(course);
        videoConvert.setOrdinalNumber(maxOrdinalNumber);
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
        Video video = getVideoById(videoUpdate.getVideoId());
        video.setUrlVideo(videoUpdate.getVideoUrl());
        video.setUrlThumbnail(videoUpdate.getThumbnailUrl());
        video.setStatus(CommonStatus.UNAVAILABLE);
        videoRepository.save(video);
    }

    @Override
    public Video getVideoById(Long videoId) {
        return videoRepository
                .findByIdAndStatus(videoId, CommonStatus.AVAILABLE)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + videoId));
    }

    @Override
    public VideoDetailResponse getAvailableVideoDetailById(Long videoId, CommonStatus commonStatus) {
        Video video = getVideoById(videoId);
        List<Video> videos = videoRepository.findByCourseAndStatus(video.getCourse(), commonStatus);

        List<VideoItemResponse> videoItemResponses = new ArrayList<>();
        if (videos != null && !videos.isEmpty()) {
            videoItemResponses = videoMapper.mapVideosToVideoItemResponses(videos);
        }
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
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoMapper.mapVideosToVideoItemResponses(videos.getContent()))
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
    public PaginationResponse<List<VideoAdminResponse>> getVideoForTeacher(String email, CommonStatus commonStatus,
            Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        List<Course> courses = courseRepository.findCourseByTeacherEmail(email);
        if (courses.isEmpty()) {
            throw new BadRequestException("Cannot found any courses with email " + email);
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

}
