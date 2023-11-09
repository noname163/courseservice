package com.example.courseservice.data.dto.response;

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
}
