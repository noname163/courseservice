package com.example.courseservice.services.videoservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.response.CourseResponse;
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
import com.example.courseservice.utils.PageableUtil;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;
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
        Video videoInsert = videoRepository.save(videoMapper.mapDtoToEntity(videoRequest));
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
        if(courses.isEmpty()){
            throw new BadRequestException("Cannot found any courses with email " + email);
        }
        if (CommonStatus.ALL.equals(commonStatus)) {
            Page<Video> videos = videoRepository.findByCourseIn(courses,pageable);
            return PaginationResponse.<List<VideoAdminResponse>>builder()
                    .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                    .totalPage(videos.getTotalPages())
                    .totalRow(videos.getTotalElements())
                    .build();
        }
        Page<Video> videos = videoRepository.findByStatusAndCourseIn(commonStatus, courses,pageable);
        return PaginationResponse.<List<VideoAdminResponse>>builder()
                .data(videoMapper.mapVideosToVideoAdminResponses(videos.getContent()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

}
