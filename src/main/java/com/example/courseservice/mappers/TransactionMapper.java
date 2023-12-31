package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.TransactionByMonth;
import com.example.courseservice.data.dto.response.TransactionResponse;
import com.example.courseservice.data.dto.response.UserTransactionResponse;
import com.example.courseservice.data.entities.Transaction;
import com.example.courseservice.data.object.TransactionByMonthInterface;
import com.example.courseservice.data.object.TransactionResponseInterface;

@Component
public class TransactionMapper {
    public TransactionResponse mapEntityToDto(Transaction transaction) {
        return TransactionResponse
                .builder()
                .id(transaction.getId())
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
                .id(transactionResponseInterface.getId())
                .subject(transactionResponseInterface.getSubject())
                .teacherName(transactionResponseInterface.getTeacherName())
                .teacherAvatar(transactionResponseInterface.getTeacherAvatar())
                .userAvatar(transactionResponseInterface.getUserAvatar())
                .userName(transactionResponseInterface.getUserName())
                .createdDate(transactionResponseInterface.getCreatedDate())
                .paymentDate(transactionResponseInterface.getPaymentDate())
                .transactionStatus(transactionResponseInterface.getTransactionStatus())
                .amount(transactionResponseInterface.getAmount())
                .courseId(transactionResponseInterface.getCourseId())
                .courseName(transactionResponseInterface.getCourseName())
                .build();
    }

    public List<UserTransactionResponse> mapToTransactionResponseList(
            List<TransactionResponseInterface> transactionResponseInterfaces) {
        return transactionResponseInterfaces.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public TransactionByMonth mapTransactionByMonth(TransactionByMonthInterface transactionByMonthInterface) {
        return TransactionByMonth.builder()
                .amount(transactionByMonthInterface.getAmount())
                .monthOfYear(transactionByMonthInterface.getMonthOfYear())
                .build();
    }

    public List<TransactionByMonth> mapTransactionByMonths(
            List<TransactionByMonthInterface> transactionByMonthInterfaces) {
        return transactionByMonthInterfaces.stream().map(this::mapTransactionByMonth).collect(Collectors.toList());
    }
}
