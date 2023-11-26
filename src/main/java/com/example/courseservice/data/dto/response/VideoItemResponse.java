package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoItemResponse {
    private long id;
    private String thumbnail;
    private String videoUrl;
    private Long like;
    private String material;
    private String name;
    private float duration;
    private LocalDateTime createDate;
    private VideoStatus videoStatus;
    private Boolean isAccess;
}
