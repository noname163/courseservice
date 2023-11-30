package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.TransactionStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserTransactionResponse {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime paymentDate;
    private TransactionStatus transactionStatus;
    private String teacherName;
    private String subject;
    private Double amount;
    private Long courseId;
    private String courseName;
    private String transactionCode;
}
