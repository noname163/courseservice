package com.example.courseservice.data.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPaymentTeacherRequest {
    private Long id;
    private String paymentCode;
    private LocalDateTime paymentDate;
    private Double amount;
}
