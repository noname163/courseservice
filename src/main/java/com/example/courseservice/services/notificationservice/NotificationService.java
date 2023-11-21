package com.example.courseservice.services.notificationservice;

import com.example.courseservice.data.dto.response.NotificationResponse;
import com.example.courseservice.data.object.NotificationContent;

public interface NotificationService {
    public void sendNotification(NotificationResponse content);
    public NotificationResponse createNotificationForCurrentUser(NotificationContent notificationContent);
}
