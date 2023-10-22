package com.example.courseservice.services.uploadservice;

import java.util.List;

import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.FileResponse;

public interface UploadService {
    public CloudinaryUrl uploadMedia(FileResponse fileResponse);
    public List<CloudinaryUrl> uploadMediaList(List<FileResponse> files);
}
