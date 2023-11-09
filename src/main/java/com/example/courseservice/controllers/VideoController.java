package com.example.courseservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.services.videoservice.VideoService;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    public ResponseEntity<VideoDetailResponse> getVideoById(Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getAvailableVideoDetailById(id));
    }
}
