package com.example.courseservice.data.dto.request;

import java.util.List;

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
    private List<VideoOrder> videoOrders;
}
