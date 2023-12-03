package com.example.courseservice.data.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.courseservice.data.constants.Validation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminPaymentTeacherRequest {
    @NotNull
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = Validation.MIN_LENGTH_PASSWORD, message = "payment code cannot smaller than 6")
    private String paymentCode;
    @NotNull
    private LocalDateTime paymentDate;
    @NotNull
    @Min(1000)
    private Double amount;
}
