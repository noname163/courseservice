package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.TransactionStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserTransactionResponse {
    private LocalDateTime createdDate;
    private LocalDateTime paymentDate;
    private TransactionStatus transactionStatus;
    private Double amount;
    private Long courseId;
    private String courseName;
}
