package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.courseservice.data.constants.ReactStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReactRequest {
    @NotNull
    private ReactStatus status;
    @NotNull
    @Size(min = 1)
    private Long videoId;
}
