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
import com.cloudinary.utils.ObjectUtils;
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

    @Override
    public String uploadMedia(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType != null && environmentVariable.initializeAllowedContentTypes().containsKey(contentType)) {
                Map<String, String> options = new HashMap<>();
                options.put("resource_type", "auto");

                var uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
                return uploadResult.get("public_id").toString();

            } else {
                throw new BadRequestException("Unsupported file type. Supported types are: "
                        + String.join(", ", environmentVariable.initializeAllowedContentTypes().values()));
            }
        } catch (IOException e) {
            throw new MediaUploadException("Failed to upload media", e);
        }
    }

    @Override
    public List<String> uploadMediaList(List<MultipartFile> files) {
        return files.stream().map(this::uploadMedia).collect(Collectors.toList());
    }

    public List<String> splitAndUploadVideo(String inputVideoUrl) throws IOException {
        List<String> result = new ArrayList<>();

        // Define the segment duration in seconds
        int segmentDuration = 10; // 10 seconds

        int startTime = 0;
        int segmentNumber = 1;

        while (true) {
            String transformation = "start_" + startTime + ",end_" + (startTime + segmentDuration);
            String segmentPublicId = "segment_" + segmentNumber;

            Map<String, String> options = ObjectUtils.asMap(
                    "transformation", transformation,
                    "public_id", segmentPublicId,
                    "overwrite", true
            );

            var uploadResult = cloudinary.uploader().upload(inputVideoUrl, options);
            System.out.println("Segment " + segmentNumber + " URL: " + uploadResult.get("secure_url"));
            result.add(uploadResult.get("secure_url").toString());
            segmentNumber++;
            startTime += segmentDuration;

            if (startTime >= getTotalVideoDuration(inputVideoUrl)) {
                break;
            }
        }
        return result;
    }

    private int getTotalVideoDuration(String videoUrl) {
        var transformation = new Transformation().videoCodec("auto");
        transformation.fetchFormat("auto");

        try {
            // Get video metadata using the Cloudinary API
            var videoInfo = cloudinary.api().resource(videoUrl, ObjectUtils.asMap("resource_type", "video", "format", "json", "video_metadata", "true"));
            var videoMetadata = (Map) videoInfo.get("video_metadata");

            if (videoMetadata != null) {
                // Extract the duration from the video metadata
                Integer duration = (Integer) videoMetadata.get("duration");

                if (duration != null) {
                    return duration;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
