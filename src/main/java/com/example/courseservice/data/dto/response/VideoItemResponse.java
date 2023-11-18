package com.example.courseservice.data.dto.response;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoItemResponse {
    private long id;
    private String name;
    private float duration;
    private VideoStatus videoStatus;
    private Boolean isAccess;
}
