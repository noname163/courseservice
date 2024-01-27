package com.example.courseservice.services.uploadservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.VideoUrls;

public interface CloudinaryService {
    public CloudinaryUrl uploadMedia(FileResponse fileResponse);

    public CloudinaryUrl uploadMedia(MultipartFile file);

    public void deleteMedia(String publicId);

    public List<CloudinaryUrl> uploadMediaList(List<FileResponse> files);

    public CloudinaryUrl uploadMaterial(FileResponse file);

    public List<VideoUrls> splitVideo(String publicId, int maxSegmentDuration,
            float videoDuration);

    public String createUrlById(String publicId, String mediaType, String subfixType);
}
