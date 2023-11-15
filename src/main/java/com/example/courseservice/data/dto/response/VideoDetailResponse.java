package com.example.courseservice.data.dto.response;

import java.util.List;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoDetailResponse {
    private String url;
    private String thumbnail;
    private String name;
    private long like;
    private VideoStatus videoStatus;
    private long dislike;
    List<VideoItemResponse> videoItemResponses;
}
