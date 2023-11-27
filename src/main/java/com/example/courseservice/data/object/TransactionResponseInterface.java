package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.TransactionStatus;

public interface TransactionResponseInterface {
    LocalDateTime getCreatedDate();
    LocalDateTime getPaymentDate();
    TransactionStatus getTransactionStatus();
    Double getAmount();
    Long getCourseId();
    String getCourseName();
}
