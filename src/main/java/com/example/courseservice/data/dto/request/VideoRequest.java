package com.example.courseservice.data.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoRequest {
    private String name;
    private Long courseId;
    private String description;
    private Integer order;
}
