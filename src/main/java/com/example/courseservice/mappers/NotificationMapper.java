package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.NotificationResponse;
import com.example.courseservice.data.entities.Notification;

@Component
public class NotificationMapper {
    public NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse
                .builder()
                .content(notification.getContent())
                .id(notification.getId())
                .title(notification.getTitle())
                .userId(notification.getUserId())
                .build();
    }

    public List<NotificationResponse> mapToNotificationResponses(List<Notification> notifications) {
        return notifications
                .stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }
}
