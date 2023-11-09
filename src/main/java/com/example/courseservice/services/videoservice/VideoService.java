package com.example.courseservice.services.videoservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.VideoUpdate;

public interface VideoService {
    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial);
    public void insertVideoUrl(VideoUpdate videoUpdate);
    public Video getVideoById(Long videoId);
    public VideoDetailResponse getAvailableVideoDetailById(Long videoId);
    public List<VideoItemResponse> getListVideoAvailableByCourse(Long courseId);
}
