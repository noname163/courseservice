package com.example.courseservice.services.uploadservice.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.MediaUploadException;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.utils.EnvironmentVariable;

@Service
public class UploadServiceImpl implements UploadService {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private EnvironmentVariable environmentVariable;

    public String uploadMedia(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType != null && environmentVariable.initializeAllowedContentTypes().containsKey(contentType)) {
                Map<String, String> options = new HashMap<>();
                options.put("resource_type", "auto");

                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
                return uploadResult.get("public_id").toString();

            } else {
                throw new BadRequestException("Unsupported file type. Supported types are: "
                        + String.join(", ", environmentVariable.initializeAllowedContentTypes().values()));
            }
        } catch (IOException e) {
            throw new MediaUploadException("Failed to upload media", e);
        }
    }

    public List<String> uploadMediaList(List<MultipartFile> files) {
        return files.stream().map(this::uploadMedia).collect(Collectors.toList());
    }
}
