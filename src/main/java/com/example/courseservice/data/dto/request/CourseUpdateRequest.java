package com.example.courseservice.data.dto.request;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.example.courseservice.data.object.Subject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseUpdateRequest {
    private Long courseId;
    private String description;
    private String name;
    @Min(1000)
    private Double price;
    private Long levelId;
    private Subject subject;
    private List<VideoOrder> videoOrders;
}
