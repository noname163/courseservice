package com.example.courseservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.dto.request.ListNotificationResponse;
import com.example.courseservice.data.entities.Notification;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.notificationservice.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAuthority('STUDENT')")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Get all notification of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all notification successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ListNotificationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping()
    public ResponseEntity<List<ListNotificationResponse>> getNotificationOfCurrentUser() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationService.getNotificationOfCurrentUser());
    }

    @Operation(summary = "Read notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all notification successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PatchMapping("/read")
    public ResponseEntity<Void> readNotification(@RequestParam(required = true) Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Get notification detail of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all notification successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ListNotificationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/detail")
    public ResponseEntity<Notification> getNotificationDetail(@RequestParam(required = true) Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getNotificationDetail(id));
    }

    @Operation(summary = "Get notification by type of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all notification successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ListNotificationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/type")
    public ResponseEntity<List<ListNotificationResponse>> getNotificationByType(
            @RequestParam(required = true) NotificationType notificationType) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(notificationService.getNotificationOfCurrentUserByType(notificationType));
    }
}
