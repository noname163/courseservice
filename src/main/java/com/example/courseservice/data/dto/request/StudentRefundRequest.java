package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class StudentRefundRequest {
    @NotNull
    private Long id;
    private String reason;
}
