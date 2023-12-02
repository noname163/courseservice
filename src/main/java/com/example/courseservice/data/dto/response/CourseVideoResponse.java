package com.example.courseservice.data.dto.response;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseVideoResponse {
    private long id;
    private String name;
    private String thumbnail;
    private String url;
    private float duration;
    private long totalLike;
    private int totalComment;
    private int ordinalNumber;
    private Boolean isWatched;
    private Boolean isDraft;
    private VideoStatus videoStatus;
}
