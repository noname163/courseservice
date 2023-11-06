package com.example.courseservice.data.object;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VideoUpdate {
    private String videoUrl;
    private String thumbnailUrl;
    private float duration;
    private Long videoId;
}
