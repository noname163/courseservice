package com.example.courseservice.mappers;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.TransactionResponse;
import com.example.courseservice.data.entities.Transaction;

@Component
public class TransactionMapper {
    public TransactionResponse mapEntityToDto(Transaction transaction) {
        return TransactionResponse
                .builder()
                .paymentDate(transaction.getPaymentDate())
                .createdDate(transaction.getCreateDate())
                .courseId(transaction.getCourse().getId())
                .amount(transaction.getCourse().getPrice())
                .transactionStatus(transaction.getStatus())
                .build();
    }
}