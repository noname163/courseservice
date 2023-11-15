package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PaymentRequest {
    private Long courseId;
    private String bankCode;
    private String language;
}
