package com.example.courseservice.data.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseDetailResponse{
    private Long id;
    private int totalStudent;
    private String description;
    private CourseResponse courseResponse;
    private List<CourseVideoResponse> videoResponse;
}
