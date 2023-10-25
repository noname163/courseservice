package com.example.courseservice.data.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMail {
    private String mailTemplate;
    private String userEmail;
    private String subject;
}
