package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.TransactionStatus;

public interface TransactionResponseInterface {
    Long getId();
    String getTransactionCode();
    LocalDateTime getCreatedDate();
    LocalDateTime getPaymentDate();
    String getSubject();
    String getTeacherName();
    String getTeacherAvatar();
    String getUserName();
    String getUserAvatar();
    TransactionStatus getTransactionStatus();
    Double getAmount();
    Long getCourseId();
    String getCourseName();
}
