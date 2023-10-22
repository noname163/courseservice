package com.example.courseservice.services.uploadservice;

import java.util.List;

import com.example.courseservice.data.dto.response.FileResponse;

public interface UploadService {
    public String uploadMedia(FileResponse fileResponse);
    public List<String> uploadMediaList(List<FileResponse> files);
}
