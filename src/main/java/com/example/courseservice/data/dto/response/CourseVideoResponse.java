package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseVideoResponse {
    private long id;
    private String name;
    private float duration;
    private long totalLike;
    private int totalComment;
}
