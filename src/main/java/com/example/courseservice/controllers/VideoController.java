package com.example.courseservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.videoservice.VideoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @Operation(summary = "Get video detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoDetailResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    public ResponseEntity<VideoDetailResponse> getVideoById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getAvailableVideoDetailById(id));
    }
}
