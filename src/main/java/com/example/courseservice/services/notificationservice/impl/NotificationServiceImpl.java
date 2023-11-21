package com.example.courseservice.services.notificationservice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.NotificationTitle;
import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.dto.response.NotificationResponse;
import com.example.courseservice.data.entities.Notification;
import com.example.courseservice.data.object.NotificationContent;
import com.example.courseservice.data.object.NotificationTemplate;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.NotificationRepository;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.notificationservice.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendNotification(NotificationResponse content) {
        String sendTo = NotificationTitle.SEND_URL+content.getUserId();
        simpMessagingTemplate.convertAndSend(sendTo, content); 
    }

    @Override
    public NotificationResponse createNotificationForCurrentUser(NotificationContent notificationContent) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        NotificationTemplate notificationTemplate = NotificationTemplate.builder().build();
        if (notificationContent.getType().equals(NotificationType.TRANSACTION)) {
            notificationTemplate = paymentNotificationTemplate(notificationContent, currentUser.getFullname());
        }
        Notification notification = notificationRepository.save(Notification
                .builder()
                .content(notificationTemplate.getContent())
                .title(notificationTemplate.getTitle())
                .userId(currentUser.getId())
                .isReading(false)
                .sendTo(currentUser.getEmail())
                .receiverName(currentUser.getFullname())
                .build());
        return NotificationResponse
                .builder()
                .content(notificationTemplate.getContent())
                .id(notification.getId())
                .userId(currentUser.getId())
                .build();

    }

    private NotificationTemplate paymentNotificationTemplate(NotificationContent notificationContent, String fullName) {
        String title = NotificationTitle.PAYMENT_TITLE + notificationContent.getCourse();
        String content = NotificationTitle.OPENING + fullName + "/n" +
                "Bạn đã thanh toán thành công khóa học " + notificationContent.getCourse() + "/n"
                + "Vào ngày " + notificationContent.getDate() + " với mức giá " + notificationContent.getPrice() + "/n"
                + "Chúc bạn học tập hiệu quả.";
        return NotificationTemplate
                .builder()
                .content(content)
                .title(title)
                .build();
    }
}
