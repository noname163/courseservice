package com.example.courseservice.data.object;

import java.time.LocalDate;

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
    private LocalDate date;
    private Double price;
    private String content;
    private String course;
    private NotificationType type;
}
