package com.example.courseservice.data.dto.response;

import java.util.List;

import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoDetailResponse {
    private String url;
    private Long courseId;
    private String thumbnail;
    private String description;
    private String name;
    private long like;
    private long dislike;
    private String material;
    private ReactStatus reactStatus;
    private VideoStatus videoStatus;
    private Float duration;
    List<VideoItemResponse> videoItemResponses;
}
