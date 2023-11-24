package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VideoTemporaryUpdateRequest {
    @NotNull(message = "Video temporary id is require")
    private Long videoTemporaryId;
    private String name;
    private String description;
    private VideoStatus videoStatus;
}
