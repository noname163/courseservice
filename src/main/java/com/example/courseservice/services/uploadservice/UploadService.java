package com.example.courseservice.services.uploadservice;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    public String uploadMedia(MultipartFile file);
    public List<String> uploadMediaList(List<MultipartFile> files);
    public List<String> splitAndUploadVideo(String url) throws IOException;
}
