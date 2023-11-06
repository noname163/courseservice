package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VideoResponse {
    private long videoId;
    private FileResponse video;
    private FileResponse thumbnail;
}
