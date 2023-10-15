package com.example.courseservice.services.uploadservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    public String uploadMedia(MultipartFile file);
    public List<String> uploadMediaList(List<MultipartFile> files);
}
