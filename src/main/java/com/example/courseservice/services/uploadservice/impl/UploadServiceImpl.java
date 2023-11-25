package com.example.courseservice.services.uploadservice.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.VideoUrls;
import com.example.courseservice.data.object.MediaType;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.MediaUploadException;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.utils.EnvironmentVariable;
import com.example.courseservice.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UploadServiceImpl implements UploadService {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EnvironmentVariable environmentVariable;
    @Autowired
    private StringUtil stringUtil;

    @Override
    public CloudinaryUrl uploadMedia(FileResponse file) {
        try {
            // Check if the content type is supported
            String contentType = file.getContentType();
            if (contentType == null || !environmentVariable.initializeAllowedContentTypes().containsKey(contentType)) {
                throw new BadRequestException("Unsupported file type. Supported types are: "
                        + String.join(", ", environmentVariable.initializeAllowedContentTypes().values()));
            }

            MediaType mediaType = stringUtil.convertStringToMediaType(contentType);
            // Define upload options
            Map<String, String> options = new HashMap<>();
            options.put("resource_type", "auto");

            log.info("Start uploading file: {}", file.getFileName());
            // Perform the upload
            var uploadResult = cloudinary.uploader().upload(file.getFileStorage(), options);

            // Extract information from the upload result
            String publicId = uploadResult.get("public_id").toString();
            String url = createUrlById(publicId, mediaType.getMediaType(), mediaType.getSubfix());
            float videoDuration = 0;
            if (uploadResult.get("duration") != null) {
                videoDuration = Float.parseFloat(uploadResult.get("duration").toString());
            }

            log.info("Upload successful. URL: {}", url);

            // Create and return a CloudinaryUrl object
            return CloudinaryUrl.builder()
                    .url(url)
                    .publicId(publicId)
                    .duration(videoDuration)
                    .build();
        } catch (IOException e) {
            throw new MediaUploadException("Failed to upload media", e);
        }
    }

    @Override
    public List<CloudinaryUrl> uploadMediaList(List<FileResponse> files) {
        return files.stream().map(this::uploadMedia).collect(Collectors.toList());
    }

    @Override
    public List<VideoUrls> splitVideo(String publicId, int maxSegmentDuration,
            float videoDuration) {
        List<VideoUrls> videoSegments = new ArrayList<>();
        int startTime = 0;
        int endTime = maxSegmentDuration;

        while (true) {
            var transformation = new Transformation<>()
                    .y(5).x(10).color("#eee")
                    .startOffset(startTime)
                    .duration(maxSegmentDuration)
                    .chain();
            String url = cloudinary.url()
                    .resourceType("video")
                    .transformation(transformation)
                    .generate(publicId + ".mp4");
            videoSegments.add(VideoUrls.builder()
                    .url(url)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build());

            startTime = endTime;
            endTime += maxSegmentDuration;
            videoDuration -= maxSegmentDuration;

            if (endTime > videoDuration && videoDuration > 0) {
                startTime = endTime;
                endTime = (int) videoDuration;
            }
            if (endTime > videoDuration && videoDuration < 0) {
                break;
            }
        }
        try {
            log.info("List video url {}", objectMapper.writeValueAsString(videoSegments));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return videoSegments;
    }

    @Override
    public String createUrlById(String id, String mediaType, String subfixType) {
        return cloudinary.url()
                .resourceType(mediaType)
                .generate(id + "." + subfixType);
    }

    @Override
    public CloudinaryUrl uploadMetrial(FileResponse file) {
        try {
            // Check if the content type is supported
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                throw new BadRequestException("Unsupported file type. Supported types are: PDF, DOCX");
            }

            MediaType mediaType = stringUtil.convertStringToMediaType(contentType);
            // Define upload options
            Map<String, String> options = new HashMap<>();
            options.put("resource_type", "auto");

            log.info("Start uploading file: {}", file.getFileName());
            // Perform the upload
            var uploadResult = cloudinary.uploader().upload(file.getFileStorage(), options);

            // Extract information from the upload result
            String publicId = uploadResult.get("public_id").toString();
            String url = createUrlById(publicId, mediaType.getMediaType(), mediaType.getSubfix());
            float videoDuration = 0;
            if (uploadResult.get("duration") != null) {
                videoDuration = Float.parseFloat(uploadResult.get("duration").toString());
            }

            log.info("Upload successful. URL: {}", url);

            // Create and return a CloudinaryUrl object
            return CloudinaryUrl.builder()
                    .url(url)
                    .publicId(publicId)
                    .duration(videoDuration)
                    .build();
        } catch (IOException e) {
            throw new MediaUploadException("Failed to upload media", e);
        }
    }

    @Override
    public CloudinaryUrl uploadMedia(MultipartFile file) {
       try {
            // Check if the content type is supported
            String contentType = file.getContentType();
            if (contentType == null || !environmentVariable.initializeAllowedContentTypes().containsKey(contentType)) {
                throw new BadRequestException("Unsupported file type. Supported types are: "
                        + String.join(", ", environmentVariable.initializeAllowedContentTypes().values()));
            }

            MediaType mediaType = stringUtil.convertStringToMediaType(contentType);
            // Define upload options
            Map<String, String> options = new HashMap<>();
            options.put("resource_type", "auto");

            log.info("Start uploading file: {}", file.getOriginalFilename());
            // Perform the upload
            var uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            // Extract information from the upload result
            String publicId = uploadResult.get("public_id").toString();
            String url = createUrlById(publicId, mediaType.getMediaType(), mediaType.getSubfix());
            float videoDuration = 0;
            if (uploadResult.get("duration") != null) {
                videoDuration = Float.parseFloat(uploadResult.get("duration").toString());
            }

            log.info("Upload successful. URL: {}", url);

            // Create and return a CloudinaryUrl object
            return CloudinaryUrl.builder()
                    .url(url)
                    .publicId(publicId)
                    .duration(videoDuration)
                    .build();
        } catch (IOException e) {
            throw new MediaUploadException("Failed to upload media", e);
        }
    }
}
