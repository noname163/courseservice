package com.example.courseservice.data.dto.request;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoRequest {
    private String name;
    private Long courseId;
    private String description;
    private VideoStatus videoStatus;
    private Integer order;
}
