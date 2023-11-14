package com.example.courseservice.services.videotmpservice;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.VideoTemporary;
import com.example.courseservice.data.object.VideoUpdate;

public interface VideoTmpService {
    public VideoResponse saveTempVideo(VideoUpdateRequest videoUpdateRequest, MultipartFile video, MultipartFile thumbnail);
    public void insertVideoUrl(VideoUpdate videoUpdate);
}
