package com.example.courseservice.data.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class AdminDasboardResponse {
    private Double monthlyIncome;
    private Long totalCourse;
    private Long totalVideo;
    private List<TransactionByMonth> transactionByMonths;
}
