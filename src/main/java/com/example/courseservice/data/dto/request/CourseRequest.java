package com.example.courseservice.data.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRequest {
    private String description;
    private String name;
    private Double price;
    private String subject;
    private long levelId;
    private List<String> topic;
}
