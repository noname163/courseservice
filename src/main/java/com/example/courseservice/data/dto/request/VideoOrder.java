package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoOrder {
    private Long videoId;
    private Integer videoOrder;
    private Boolean isDraft;
}
