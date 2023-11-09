package com.example.courseservice.services.videoservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.VideoMapper;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.videoservice.VideoService;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideoMapper videoMapper;
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
    public VideoDetailResponse getAvailableVideoDetailById(Long videoId) {
        Video video = videoRepository
                .findById(videoId)
                .orElseThrow(() -> new BadRequestException("Cannot found video with id " + videoId));
        List<Video> videos = videoRepository.findByCourse(video.getCourse());

        List<VideoItemResponse> videoItemResponses = new ArrayList<>();
        if (videos != null && !videos.isEmpty()) {
            videoItemResponses = videoMapper.mapVideosToVideoItemResponses(videos);
        }
        VideoDetailResponse videoResponse = videoMapper.mapEntityToDto(video);
        videoResponse.setVideoItemResponses(videoItemResponses);
        return videoResponse;
    }

    @Override
    public List<VideoItemResponse> getListVideoAvailableByCourse(Long courseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getListVideoAvailableByCourse'");
    }

}
