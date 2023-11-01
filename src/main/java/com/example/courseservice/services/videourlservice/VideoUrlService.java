package com.example.courseservice.services.videourlservice;

import java.util.List;

import com.example.courseservice.data.dto.response.VideoUrls;

public interface VideoUrlService {
    public void insertVideoUrl(List<VideoUrls> videoUrlsRequest, long videoId);
}
