package com.example.courseservice.services.notificationservice;

import java.util.List;

import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.dto.request.ListNotificationResponse;
import com.example.courseservice.data.dto.response.NotificationResponse;
import com.example.courseservice.data.entities.Notification;
import com.example.courseservice.data.object.NotificationContent;

public interface NotificationService {
    public void sendNotification(NotificationResponse content);
    public NotificationResponse createNotificationForCurrentUser(NotificationContent notificationContent);
    public List<ListNotificationResponse> getNotificationOfCurrentUser();
    public List<ListNotificationResponse> getNotificationOfCurrentUserByType(NotificationType notificationType);
    public Notification getNotificationDetail(Long id);
    public void readNotification(Long id);
}
