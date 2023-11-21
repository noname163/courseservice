package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String content;
    private Integer numberOfNotification;
}
