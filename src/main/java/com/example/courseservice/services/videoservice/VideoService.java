package com.example.courseservice.services.videoservice;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.VideoUpdate;

public interface VideoService {
    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial);
    public void insertVideoUrl(VideoUpdate videoUpdate);
    public Video getVideoById(Long videoId);
}
