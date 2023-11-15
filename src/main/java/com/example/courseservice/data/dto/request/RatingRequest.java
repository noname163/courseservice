package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RatingRequest {
    private Long courseId;
    private Integer rating;
    private String content;
}
