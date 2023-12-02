package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PaymentRequest {
    @NotNull
    @Size(min = 1)
    private Long courseId;
    @NotBlank
    @NotNull
    private String bankCode;
    @NotBlank
    @NotNull
    private String language;
}
