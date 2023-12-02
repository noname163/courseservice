package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class StudentRefundRequest {
    @NotNull
    @Size(min = 1)
    private Long id;
    private String reason;
}
