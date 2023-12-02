package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.courseservice.data.constants.VerifyStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRefundAction {
    @NotNull
    private Long id;
    private String reason;
    private String transactionCode;
    @NotNull
    private VerifyStatus verifyStatus;
}
