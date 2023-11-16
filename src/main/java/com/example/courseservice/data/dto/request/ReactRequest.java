package com.example.courseservice.data.dto.request;

import com.example.courseservice.data.constants.ReactStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReactRequest {
    private ReactStatus status;
    private Long videoId;
}
