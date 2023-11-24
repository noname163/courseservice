package com.example.courseservice.data.dto.request;

import java.util.List;

import com.example.courseservice.data.object.Subject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseTemporaryUpdateRequest {
    private Long courseId;
    private String description;
    private String name;
    private Double price;
    private Long levelId;
    private Subject subject;
    private List<VideoOrder> videoOrders;
}
