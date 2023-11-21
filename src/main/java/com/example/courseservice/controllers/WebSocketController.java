package com.example.courseservice.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.courseservice.data.dto.response.NotificationResponse;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class WebSocketController {

    @MessageMapping("/notify")
    @SendTo("/topic/notification/{id}")
    public NotificationResponse notificationUser(NotificationResponse notificationResponse){
        log.info("Sending notification........................");
        return notificationResponse;
    }
}
