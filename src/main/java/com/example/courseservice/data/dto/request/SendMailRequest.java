package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SendMailRequest {
    private String mailTemplate;
    private String userEmail;
    private String subject;
}
