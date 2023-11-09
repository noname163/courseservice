package com.example.courseservice.data.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoDetailResponse {
    private String url;
    private String name;
    private long like;
    private long dislike;
    List<VideoItemResponse> videoItemResponses;
}
