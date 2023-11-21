package com.example.courseservice.services.notificationservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.NotificationTitle;
import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.dto.request.ListNotificationResponse;
import com.example.courseservice.data.dto.response.NotificationResponse;
import com.example.courseservice.data.entities.Notification;
import com.example.courseservice.data.object.NotificationContent;
import com.example.courseservice.data.object.NotificationTemplate;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.NotificationRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.NotificationMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.notificationservice.NotificationService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendNotification(NotificationResponse content) {
        log.info("Sending notification for user id " + content.getUserId());
        String sendTo = NotificationTitle.SEND_URL + content.getUserId();
        simpMessagingTemplate.convertAndSend(sendTo, content);
        log.info("Send notification success");
    }

    @Override
    public NotificationResponse createNotificationForCurrentUser(NotificationContent notificationContent) {
        NotificationTemplate notificationTemplate = NotificationTemplate.builder().build();
        if (notificationContent.getType().equals(NotificationType.TRANSACTION)) {
            notificationTemplate = paymentNotificationTemplate(notificationContent, notificationContent.getEmail());
        }
        if (notificationContent.getType().equals(NotificationType.SYSTEM)) {
            notificationTemplate = systemNotificationTemplate(notificationContent, notificationContent.getEmail());
        }
        if (notificationContent.getType().equals(NotificationType.LEARNING)) {
            notificationTemplate = learnNotificationTemplate(notificationContent, notificationContent.getEmail());
        }

        Notification notification = notificationRepository.save(Notification
                .builder()
                .content(notificationTemplate.getContent())
                .title(notificationTemplate.getTitle())
                .userId(notificationContent.getUserId())
                .isReading(false)
                .notificationType(notificationContent.getType())
                .sendTo(notificationContent.getEmail())
                .build());

        log.info("Start saving notification");

        return NotificationResponse
                .builder()
                .content(notificationTemplate.getContent())
                .id(notification.getId())
                .userId(notificationContent.getUserId())
                .build();

    }

    private NotificationTemplate paymentNotificationTemplate(NotificationContent notificationContent, String fullName) {
        log.info("Start creating notification for user " + fullName);
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

    private NotificationTemplate systemNotificationTemplate(NotificationContent notificationContent, String fullName) {
        log.info("Start creating notification for user " + fullName);
        String title = NotificationTitle.SYSTEM_TITLE + notificationContent.getCourse();
        String content = NotificationTitle.OPENING + fullName + "\n" +
                notificationContent.getReplyEmail() + " đã trả lời bình luận của bạn \n"
                + "Chúc bạn học tập hiệu quả.";
        return NotificationTemplate
                .builder()
                .content(content)
                .title(title)
                .build();
    }

    private NotificationTemplate learnNotificationTemplate(NotificationContent notificationContent, String fullName) {
        log.info("Start creating notification for user " + fullName);
        String title = NotificationTitle.LEARNING_TITLE + notificationContent.getCourse();
        String content = NotificationTitle.OPENING + fullName + "\n" +
                "Bạn đã tham gia " + notificationContent.getCourse() + "\n"
                + "Chúc bạn học tập hiệu quả.";
        return NotificationTemplate
                .builder()
                .content(content)
                .title(title)
                .build();
    }

    @Override
    public List<ListNotificationResponse> getNotificationOfCurrentUser() {
        UserInformation currentUser = securityContextService.getCurrentUser();

        List<Notification> systemNotifications = notificationRepository
                .findByUserIdAndNotificationTypeAndIsReadingTrue(currentUser.getId(), NotificationType.SYSTEM);
        List<Notification> learnNotifications = notificationRepository
                .findByUserIdAndNotificationTypeAndIsReadingTrue(currentUser.getId(), NotificationType.LEARNING);
        List<Notification> paymentNotifications = notificationRepository
                .findByUserIdAndNotificationTypeAndIsReadingTrue(currentUser.getId(), NotificationType.TRANSACTION);

        List<NotificationResponse> systemNotificationsResponse = notificationMapper
                .mapToNotificationResponses(systemNotifications);
        List<NotificationResponse> learnNotificationsResponse = notificationMapper
                .mapToNotificationResponses(learnNotifications);
        List<NotificationResponse> paymentNotificationsResponse = notificationMapper
                .mapToNotificationResponses(paymentNotifications);

        List<ListNotificationResponse> listNotificationResponses = new ArrayList<>();

        listNotificationResponses.add(ListNotificationResponse
                .builder()
                .notificationResponses(paymentNotificationsResponse)
                .notificationType(NotificationType.TRANSACTION)
                .totalNotification(paymentNotifications.size())
                .build());
        listNotificationResponses.add(ListNotificationResponse
                .builder()
                .notificationResponses(learnNotificationsResponse)
                .notificationType(NotificationType.LEARNING)
                .totalNotification(learnNotificationsResponse.size())
                .build());
        listNotificationResponses.add(ListNotificationResponse
                .builder()
                .notificationResponses(systemNotificationsResponse)
                .notificationType(NotificationType.LEARNING)
                .totalNotification(systemNotificationsResponse.size())
                .build());

        return listNotificationResponses;
    }

    @Override
    public Notification getNotificationDetail(Long id) {
        Long userId = securityContextService.getCurrentUser().getId();
        return notificationRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new BadRequestException("Cannot found notification with id " + id));
    }

    @Override
    public void readNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Cannot found notification with id " + id));

        notification.setIsReading(true);
        notificationRepository.save(notification);
    }

    @Override
    public List<ListNotificationResponse> getNotificationOfCurrentUserByType(NotificationType notificationType) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        List<Notification> notifications = notificationRepository
                .findByUserIdAndNotificationTypeAndIsReadingTrue(currentUser.getId(), notificationType);

        List<NotificationResponse> notificationsResponse = notificationMapper
                .mapToNotificationResponses(notifications);
        List<ListNotificationResponse> listNotificationResponses = new ArrayList<>();

        listNotificationResponses.add(ListNotificationResponse
                .builder()
                .notificationResponses(notificationsResponse)
                .notificationType(notificationType)
                .totalNotification(notificationsResponse.size())
                .build());
        return listNotificationResponses;
    }
}
