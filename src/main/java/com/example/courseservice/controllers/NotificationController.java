package com.example.courseservice.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.courseservice.data.dto.response.NotificationResponse;

@Controller
public class NotificationController {

    @MessageMapping("/notify")
    @SendTo("/topic/notifiction/{id}")
    public NotificationResponse notificationUser(NotificationResponse notificationResponse){
        return notificationResponse;
    }
}
