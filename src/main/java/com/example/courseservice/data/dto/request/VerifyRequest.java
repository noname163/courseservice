package com.example.courseservice.data.dto.request;

import com.example.courseservice.data.constants.VerifyStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VerifyRequest {
    private VerifyStatus verifyStatus;
    private String reason;
    private Long id;
}
