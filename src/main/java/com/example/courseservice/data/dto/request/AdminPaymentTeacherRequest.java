package com.example.courseservice.data.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.courseservice.data.constants.Validation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPaymentTeacherRequest {
    @NotNull
    @Size(min = 1, message = "ID cannot smaller than 1")
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = Validation.MIN_LENGTH_PASSWORD, message = "payment code cannot smaller than 6")
    private String paymentCode;
    @NotNull
    private LocalDateTime paymentDate;
    @NotNull
    @Size(min = 1000, message = "Payment amount cannot smaller than 1000")
    private Double amount;
}
