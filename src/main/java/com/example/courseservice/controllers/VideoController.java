package com.example.courseservice.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.event.EventPublisher;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.fileservice.FileService;
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
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private FileService fileService;

    @Operation(summary = "Create video for new course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accepted."),
            @ApiResponse(responseCode = "400", description = "File not valid.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))
            })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("")
    public ResponseEntity<Void> createVideo(
            @RequestPart VideoRequest videoRequest,
            @RequestPart(required = true) MultipartFile video,
            @RequestPart(required = true) MultipartFile thumbnail,
            @RequestPart(required = false) MultipartFile material) throws IOException {
        eventPublisher.publishEvent(fileService.convertFileToFileResponse(video, thumbnail, material),
                videoService.saveVideoInformation(videoRequest));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Get video detail by video id for student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoItemResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/user/detail")
    public ResponseEntity<VideoItemResponse> getVideoById(@RequestParam(required = true) Long id,
            @RequestParam(required = true) CommonStatus commonStatus) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getVideoDetailById(id, commonStatus));
    }

    @Operation(summary = "Get list video by course id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoItemResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping()
    public ResponseEntity<PaginationResponse<List<VideoItemResponse>>> getListVideoByCourseId(
            @RequestParam(required = true) Long courseId,
            @RequestParam(required = true) CommonStatus commonStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getListVideoAvailableByCourse(commonStatus, courseId, page, size,
                        field,
                        sortType));
    }

    @Operation(summary = "Get list video by teacher email for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoAdminResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/user")
    public ResponseEntity<PaginationResponse<List<VideoItemResponse>>> getListVideoByTeacherEmail(
            @RequestParam(required = true) String email,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getVideoForUser(email, page, size, field,
                        sortType));
    }

    @Operation(summary = "Edit video temporary video for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Edit temporary video successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PutMapping("/teacher/edit-video")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Void> editTemporaryVideo(
            @RequestPart VideoUpdateRequest videoRequest,
            @RequestPart(required = false) MultipartFile video,
            @RequestPart(required = false) MultipartFile thumbnail,
            @RequestPart(required = false) MultipartFile material) {
        eventPublisher.publishEvent(fileService.convertFileToFileResponse(video, thumbnail, material),
                videoService.updateVideo(videoRequest));
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "Delete video for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete video successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @DeleteMapping()
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Void> deleteVideo(
            @RequestParam(required = true) Long videoId) {
        videoService.deleteVideo(videoId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "Update video order for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete temporary video successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PutMapping("/order")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Void> updateVideoOrder(@RequestBody List<VideoOrder> videoOrder,
            @RequestParam(required = false) Long courseId) {
        videoService.updateVideoOrder(videoOrder, courseId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
