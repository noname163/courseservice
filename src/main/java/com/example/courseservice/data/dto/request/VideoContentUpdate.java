package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VideoContentUpdate {
    @NotNull(message = "Video  id is require")
    private Long videoId;
    private String name;
    private String description;
    private VideoStatus videoStatus;
}
