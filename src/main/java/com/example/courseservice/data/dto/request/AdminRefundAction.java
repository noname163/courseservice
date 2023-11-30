package com.example.courseservice.data.dto.request;

import com.example.courseservice.data.constants.VerifyStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRefundAction {
    private String reason;
    private Long id;
    private VerifyStatus verifyStatus;
}
