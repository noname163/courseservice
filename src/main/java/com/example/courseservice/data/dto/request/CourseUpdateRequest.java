package com.example.courseservice.data.dto.request;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.example.courseservice.data.object.Topic;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseUpdateRequest {
    private Long courseId;
    private String description;
    private String name;
    @Min(1000)
    private Double price;
    private Long levelId;
    private List<VideoOrder> videoOrders;
}
