package com.example.courseservice.data.object;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.courseservice.data.constants.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationContent {
    private LocalDateTime date;
    private Double price;
    private String content;
    private String course;
    private String email;
    private String replyEmail;
    private String title;
    private Long userId;
    private NotificationType type;
}
