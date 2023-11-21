package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification,Long>{
    
}
