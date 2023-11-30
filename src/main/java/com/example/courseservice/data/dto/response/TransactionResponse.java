package com.example.courseservice.data.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.courseservice.data.constants.TransactionStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonSerialize
public class TransactionResponse implements Serializable {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime paymentDate;
    private TransactionStatus transactionStatus;
    private Double amount;
    private Long courseId;
}
