package com.example.courseservice.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.event.EventPublisher;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.videoservice.VideoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private VideoService videoService;

    @Operation(summary = "Upload Video")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Accepted."),
        @ApiResponse(responseCode = "400", description = "File not valid.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))
        })
    })
    @PostMapping("/video")
    public ResponseEntity<Void> uploadVideo(
            @RequestPart VideoRequest videoRequest,
            @RequestPart() MultipartFile video,
            @RequestPart() MultipartFile thumbnail) throws IOException {
        eventPublisher.publishEvent(videoService.saveVideo(videoRequest, video, thumbnail));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

