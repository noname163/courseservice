package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RatingRequest {
    @NotNull
    private Long courseId;
    @NotNull
    private Integer rating;
    private String content;
}
