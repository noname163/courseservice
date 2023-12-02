package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionByMonth {
    private Double amount;
    private String monthOfYear;
}
