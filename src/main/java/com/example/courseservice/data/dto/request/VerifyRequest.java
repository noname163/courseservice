package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @Size(min = 1)
    private Long id;
}
