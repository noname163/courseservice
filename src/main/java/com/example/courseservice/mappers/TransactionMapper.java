package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.TransactionResponse;
import com.example.courseservice.data.dto.response.UserTransactionResponse;
import com.example.courseservice.data.entities.Transaction;
import com.example.courseservice.data.object.TransactionResponseInterface;

@Component
public class TransactionMapper {
    public TransactionResponse mapEntityToDto(Transaction transaction) {
        return TransactionResponse
                .builder()
                .paymentDate(transaction.getPaymentDate())
                .createdDate(transaction.getCreatedDate())
                .courseId(transaction.getCourse().getId())
                .amount(transaction.getCourse().getPrice())
                .transactionStatus(transaction.getStatus())
                .build();
    }

     public UserTransactionResponse mapToTransactionResponse(TransactionResponseInterface transactionResponseInterface) {
        if (transactionResponseInterface == null) {
            return null;
        }

        return UserTransactionResponse.builder()
                .createdDate(transactionResponseInterface.getCreatedDate())
                .paymentDate(transactionResponseInterface.getPaymentDate())
                .transactionStatus(transactionResponseInterface.getTransactionStatus())
                .amount(transactionResponseInterface.getAmount())
                .courseId(transactionResponseInterface.getCourseId())
                .courseName(transactionResponseInterface.getCourseName())
                .build();
    }

    public List<UserTransactionResponse> mapToTransactionResponseList(List<TransactionResponseInterface> transactionResponseInterfaces) {
        return transactionResponseInterfaces.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }
}
