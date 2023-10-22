package com.example.courseservice.services.uploadservice.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.transformation.TextLayer;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.MediaUploadException;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.utils.EnvironmentVariable;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UploadServiceImpl implements UploadService {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private EnvironmentVariable environmentVariable;

    @Autowired
    private BlobServiceClient blobServiceClient;

    @Autowired
    private BlobContainerClient blobContainerClient;

    @Override
    public String uploadMedia(FileResponse file) {
        try {
            if (file.getContentType() != null
                    && environmentVariable.initializeAllowedContentTypes().containsKey(file.getContentType())) {
                Map<String, String> options = new HashMap<>();
                options.put("resource_type", "auto");
                log.info("----------------Start upload file name {}-----------------", file.getFileName());
                var uploadResult = cloudinary.uploader().upload(file.getFileStorage(), options);

                String publicId = uploadResult.get("public_id").toString();
                String url = cloudinary.url()
                        .resourceType("auto")
                        .generate(publicId);
                log.info("Upload success with url {}", url);
                return url;

            } else {
                throw new BadRequestException("Unsupported file type. Supported types are: "
                        + String.join(", ", environmentVariable.initializeAllowedContentTypes().values()));
            }
        } catch (IOException e) {
            throw new MediaUploadException("Failed to upload media", e);
        }
    }

    @Override
    public List<String> uploadMediaList(List<FileResponse> files) {
        return files.stream().map(this::uploadMedia).collect(Collectors.toList());
    }


}
