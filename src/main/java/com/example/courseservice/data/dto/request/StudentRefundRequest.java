package com.example.courseservice.data.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class StudentRefundRequest {
    private Long id;
    private String reason;
}
