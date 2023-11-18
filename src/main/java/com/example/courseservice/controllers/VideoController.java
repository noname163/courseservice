package com.example.courseservice.controllers;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.event.EventPublisher;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.services.videotmpservice.VideoTmpService;

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
    private VideoTmpService videoTmpService;

    @Operation(summary = "Create Video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accepted."),
            @ApiResponse(responseCode = "400", description = "File not valid.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))
            })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("")
    public ResponseEntity<Void> uploadVideo(
            @RequestPart VideoRequest videoRequest,
            @RequestPart() MultipartFile video,
            @RequestPart() MultipartFile thumbnail) throws IOException {
        eventPublisher.publishEvent(videoService.saveVideo(videoRequest, video, thumbnail));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Update Video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accepted."),
            @ApiResponse(responseCode = "400", description = "File not valid.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))
            })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/updload")
    public ResponseEntity<Void> updateVideo(
            @RequestPart VideoRequest videoRequest,
            @RequestPart(required = true) MultipartFile video,
            @RequestPart(required = true) MultipartFile thumbnail) throws IOException {
        eventPublisher.publishEvent(videoService.uploadVideoByCourse(videoRequest, video, thumbnail));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Get video detail by video id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoDetailResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    public ResponseEntity<VideoDetailResponse> getVideoById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getAvailableVideoDetailById(id, CommonStatus.AVAILABLE));
    }

    @Operation(summary = "Get video detail by video id for teacher and admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoDetailResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/teacher/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<VideoDetailResponse> getVideoForTeacherAdminById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getVideoDetailByIdExcept(id, CommonStatus.DELETED));
    }

    @Operation(summary = "Verify video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Verify video successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/verify-video")
    public ResponseEntity<Void> verifyCourse(@Valid @RequestBody VerifyRequest verifyRequest) {
        videoService.verifyVideo(verifyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get list video course id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoItemResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT', 'TEACHER')")
    public ResponseEntity<PaginationResponse<List<VideoItemResponse>>> getListVideoByCourseId(
            @RequestParam(required = true) Long courseId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getListVideoAvailableByCourse(courseId, page, size, field,
                        sortType));
    }

    @Operation(summary = "Get list video for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoAdminResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/teacher")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<PaginationResponse<List<VideoAdminResponse>>> getListVideoForCurrentTeacher(
            @RequestParam(required = true, defaultValue = "ALL") CommonStatus commonStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getVideoForTeacher(commonStatus, page, size, field,
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
    public ResponseEntity<PaginationResponse<List<VideoAdminResponse>>> getListVideoByTeacherEmail(
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

    @Operation(summary = "Get list video for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoAdminResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PaginationResponse<List<VideoAdminResponse>>> getListVideoForAdmin(
            @RequestParam(required = true, defaultValue = "ALL") CommonStatus commonStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoService.getVideoForAdmin(commonStatus, page, size, field,
                        sortType));
    }

    @Operation(summary = "Get list video update for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoAdminResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/admin/update-list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PaginationResponse<List<VideoAdminResponse>>> getListVideoUpdateForAdmin(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoTmpService.getUpdateVideo(page, size, field,
                        sortType));
    }
}
