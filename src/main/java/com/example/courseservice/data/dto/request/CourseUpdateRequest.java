package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseUpdateRequest {
    private Long courseId;
    private String description;
    private String name;
    private Double price;
    private Long levelId;
}
