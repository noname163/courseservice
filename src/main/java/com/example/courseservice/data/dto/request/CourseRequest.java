package com.example.courseservice.data.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRequest {
    private MultipartFile thumbinial;
    private String description;
    private String name;
    private Double price;
    private String subject;
    private int levelId;
}
