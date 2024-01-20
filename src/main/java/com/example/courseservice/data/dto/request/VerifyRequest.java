package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;

import com.example.courseservice.data.constants.VerifyStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VerifyRequest {
    @NotNull
    private VerifyStatus verifyStatus;
    private String reason;
    @NotNull
    private Long id;
}
