package com.example.courseservice.services.uploadservice;

import java.util.List;

import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.VideoUrls;

public interface UploadService {
    public CloudinaryUrl uploadMedia(FileResponse fileResponse);
    public List<CloudinaryUrl> uploadMediaList(List<FileResponse> files);
    public List<VideoUrls> splitVideo(String publicId, int maxSegmentDuration,
            float videoDuration);
}
