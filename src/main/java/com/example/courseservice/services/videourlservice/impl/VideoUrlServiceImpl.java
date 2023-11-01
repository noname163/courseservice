package com.example.courseservice.services.videourlservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.dto.response.VideoUrls;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.entities.VideoUrl;
import com.example.courseservice.data.repositories.VideoUrlRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.services.videourlservice.VideoUrlService;

@Service
public class VideoUrlServiceImpl implements VideoUrlService {
    @Autowired
    private VideoUrlRepository videoUrlRepository;
    @Autowired
    private VideoService videoService;

    @Override
    public void insertVideoUrl(List<VideoUrls> videoUrlsRequest, long videoId) {
        if (videoUrlsRequest ==null || videoUrlsRequest.isEmpty()) {
            throw new BadRequestException("Video url object null");
        }
        Video video = videoService.getVideoById(videoId);
        List<VideoUrl> videoUrls = new ArrayList<>();
        for (VideoUrls videoUrlsResponse : videoUrlsRequest) {
            videoUrls.add(VideoUrl
                    .builder()
                    .video(video)
                    .url(videoUrlsResponse.getUrl())
                    .startTime(videoUrlsResponse.getStartTime())
                    .endTime(videoUrlsResponse.getEndTime())
                    .build());
        }
        videoUrlRepository.saveAll(videoUrls);
    }

}
