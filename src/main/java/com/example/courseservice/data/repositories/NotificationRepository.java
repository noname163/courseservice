package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification,Long>{
    public List<Notification> findByUserIdAndNotificationTypeAndIsReadingTrue(Long userId, NotificationType notificationType);
    public Optional<Notification> findByUserIdAndId(Long userId, Long id);
}
