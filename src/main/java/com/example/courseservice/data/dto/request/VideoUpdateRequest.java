package com.example.courseservice.data.dto.request;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VideoUpdateRequest {
    private Long videoId;
    private String name;
    private String description;
    private VideoStatus videoStatus;
    private Integer order;
}
