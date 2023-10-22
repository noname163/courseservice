package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VideoUrls {
    String url;
    Integer startTime;
    Integer endTime;
    Long videoId;
}
