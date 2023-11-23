package com.example.courseservice.services.videotmpservice.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.entities.VideoTemporary;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.CourseTemporaryRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.data.repositories.VideoTemporaryRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.VideoTemporaryMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.services.videotmpservice.VideoTmpService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class VideoTmpServiceImpl implements VideoTmpService {

    @Autowired
    private VideoTemporaryRepository videoTemporaryRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    @Lazy
    private CourseRepository courseRepository;
    @Autowired
    private VideoTemporaryMapper videoTemporaryMapper;
    @Autowired
    private CourseTemporaryRepository courseTemporaryRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private PageableUtil pageableUtil;

    @Override
    public VideoResponse updateVideo(VideoUpdateRequest videoUpdateRequest, MultipartFile video,
            MultipartFile thumbnail) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Course course = courseRepository
                .findByIdAndCommonStatusNot(videoUpdateRequest.getCourseId(), CommonStatus.DELETED)
                .orElseThrow(
                        () -> new BadRequestException("Not exist video with id " + videoUpdateRequest.getCourseId()));

        if (!course.getTeacherEmail().equals(currentUser.getEmail())) {
            throw new InValidAuthorizationException("Not this course to create video");
        }
        course.setCommonStatus(CommonStatus.UNAVAILABLE);
        courseRepository.save(course);

        CourseTemporary courseTemporary = courseTemporaryRepository.save(CourseTemporary
                .builder()
                .updateTime(LocalDateTime.now())
                .course(course)
                .build());
        VideoTemporary videoInsert = videoTemporaryRepository.save(
                VideoTemporary
                        .builder()
                        .course(course)
                        .courseTemporary(courseTemporary)
                        .createDate(LocalDateTime.now())
                        .status(CommonStatus.UPDATING)
                        .videoStatus(videoUpdateRequest.getVideoStatus())
                        .build());
        FileResponse videoFile = fileService.fileStorage(video);
        FileResponse thumbnialFile = fileService.fileStorage(thumbnail);

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
        VideoTemporary video = videoTemporaryRepository
                .findById(videoUpdate.getVideoId())
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found Video temporary with id " + videoUpdate.getVideoId()));
        video.setUrlVideo(videoUpdate.getVideoUrl());
        video.setUrlThumbnail(videoUpdate.getThumbnailUrl());
        videoTemporaryRepository.save(video);
    }

    @Override
    public void insertVideoTmpToReal(Long courseTeporaryId, Course course) {
        List<VideoTemporary> videoTemporaries = videoTemporaryRepository
                .findByCourseTemporaryIdAndStatus(courseTeporaryId, CommonStatus.DRAFT);
        if (!videoTemporaries.isEmpty()) {
            List<Video> videos = videoTemporaryMapper.mapVideosTmpToReal(videoTemporaries, course);
            videoRepository.saveAll(videos);
            videoTemporaryRepository.deleteAll(videoTemporaries);
        }
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getUpdateVideo(Integer page, Integer size, String field,
            SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<VideoTemporary> videotemp = videoTemporaryRepository.findAll(pageable);
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoTemporaryMapper.mapVideoItemResponses(videotemp.getContent()))
                .totalPage(videotemp.getTotalPages())
                .totalRow(videotemp.getTotalElements())
                .build();
    }

    @Override
    public boolean isUpdate(Long videoId) {
        return videoTemporaryRepository.existsByVideoId(videoId);
    }

    @Override
    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial) {
        CourseTemporary course = courseTemporaryRepository
                .findByIdAndStatusNot(videoRequest.getCourseId(), CommonStatus.DELETED)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + videoRequest.getCourseId()));
        Integer maxOrdinalNumber = videoTemporaryRepository.findMaxOrdinalNumberByCourse(course);

        // Set the ordinalNumber for the new video
        int ordinalNumber = maxOrdinalNumber != null ? maxOrdinalNumber + 1 : 1;
        VideoTemporary videoConvert = videoTemporaryMapper.mapDtoToEntity(videoRequest);
        videoConvert.setStatus(CommonStatus.DRAFT);
        videoConvert.setCourseTemporary(course);
        videoConvert.setOrdinalNumber(ordinalNumber);
        VideoTemporary videoInsert = videoTemporaryRepository.save(videoConvert);
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
    public List<VideoItemResponse> getVideoTemporaryByCourseTemporaryId(Long courseTemporaryId) {
        List<VideoTemporary> videoTemporaries = videoTemporaryRepository
                .findByCourseTemporaryIdAndStatus(courseTemporaryId, CommonStatus.DRAFT);
        return videoTemporaryMapper.mapVideoItemResponses(videoTemporaries);
    }

    @Override
    public void updateVideoOrder(List<VideoOrder> videoOrders, Long courseId) {

        Set<Long> videoIds = videoOrders.stream()
                .map(VideoOrder::getVideoId)
                .collect(Collectors.toSet());

        List<VideoTemporary> videosToUpdate = videoTemporaryRepository.findByCourseTemporaryIdAndIdIn(courseId, videoIds);

        Map<Long, VideoTemporary> videoMap = videosToUpdate.stream()
                .collect(Collectors.toMap(VideoTemporary::getId, Function.identity()));

        List<VideoTemporary> updatedVideos = videoOrders.stream()
                .filter(videoOrder -> videoMap.containsKey(videoOrder.getVideoId()))
                .map(videoOrder -> {
                    VideoTemporary video = videoMap.get(videoOrder.getVideoId());
                    video.setOrdinalNumber(videoOrder.getVideoOrder());
                    return video;
                })
                .collect(Collectors.toList());

        videoTemporaryRepository.saveAll(updatedVideos);
    }

}
