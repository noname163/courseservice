package com.example.courseservice.data.dto.response;

import java.util.List;

import com.example.courseservice.data.constants.NotificationType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ListNotificationResponse {
    private List<NotificationResponse> notificationResponses;
    private NotificationType notificationType;
    private Integer totalNotification;
}
