package com.example.courseservice.data.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentProgressResponse {
    private Double progress;
    private List<Long> videoComplete;
}
