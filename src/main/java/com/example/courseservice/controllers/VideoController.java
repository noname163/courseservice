package com.example.courseservice.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.example.courseservice.data.dto.request.VideoContentUpdate;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoTemporaryUpdateRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.event.EventPublisher;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetmpservice.CourseTmpService;
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
    @Autowired
    private CourseTmpService courseService;

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
        eventPublisher.publishEvent(videoTmpService.saveVideo(videoRequest, video, thumbnail, material));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Upload Video For Exsited Course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accepted."),
            @ApiResponse(responseCode = "400", description = "File not valid.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))
            })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/teacher/updload")
    public ResponseEntity<Void> updateVideo(
            @RequestPart VideoUpdateRequest videoRequest,
            @RequestPart(required = true) MultipartFile video,
            @RequestPart(required = true) MultipartFile thumbnail,
            @RequestPart(required = false) MultipartFile material) throws IOException {
        eventPublisher.publishEvent(videoTmpService.updateVideo(videoRequest, video, thumbnail, material));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Update Video Content ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accepted."),
            @ApiResponse(responseCode = "400", description = "File not valid.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))
            })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/teacher/update-content")
    public ResponseEntity<Void> updateVideoContent(
            @RequestBody VideoContentUpdate videoRequest) {
        videoService.editVideoContent(videoRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Get video detail by video id for student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoDetailResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/user/{id}")
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

    @Operation(summary = "Get temporary video detail by video id for teacher and admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoDetailResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/teacher/temporary/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<VideoAdminResponse> getTemporaryVideoForTeacherAdminById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoTmpService.getVideoTemporaryById(id));
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

    @Operation(summary = "Get list draft video for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoAdminResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/admin/draft-list")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<PaginationResponse<List<VideoItemResponse>>> getListVideoUpdateForAdmin(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoTmpService.getUpdateVideo(page, size, field,
                        sortType));
    }

    @Operation(summary = "Get list draft video for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoAdminResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/teacher/draft-list")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<PaginationResponse<List<VideoItemResponse>>> getListVideoUpdateForTeacher(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoTmpService.getUpdateVideoForCurrentUser(page, size, field,
                        sortType));
    }

    @Operation(summary = "Get list draft video by temporary course id for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoAdminResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/admin/draft-video")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<VideoItemResponse>> getListVideoByCourseTemporaryForAdmin(
            @RequestParam(required = true) Long courseId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoTmpService.getVideoTemporaryByCourseTemporaryIdForAdmin(courseId));
    }

    @Operation(summary = "Get list draft video by temporary course id for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get video successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VideoAdminResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/teacher/draft-video")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<List<VideoItemResponse>> getListVideoByCourseTemporaryForTeacher(
            @RequestParam(required = true) Long courseId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(videoTmpService.getVideoTemporaryByCourseTemporaryIdForTeacher(courseId));
    }

    @Operation(summary = "Edit video temporary video for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Edit temporary video successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PutMapping("/teacher/edit-temporary-video")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Void> editTemporaryVideo(
            @RequestPart VideoTemporaryUpdateRequest videoRequest,
            @RequestPart(required = false) MultipartFile video,
            @RequestPart(required = false) MultipartFile thumbnail,
            @RequestPart(required = false) MultipartFile material) {
        eventPublisher.publishEvent(videoTmpService.editVideoTmpById(videoRequest, video, thumbnail, material));
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

    @Operation(summary = "Delete temporary video for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete temporary video successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @DeleteMapping("/temporary-video")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<Void> deleteTemporaryVideo(
            @RequestParam(required = true) Long videoId) {
        videoTmpService.deletedTemporaryVideo(videoId);
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
            @RequestParam(required = false) Long courseId, @RequestParam(required = false) Long courseTemporaryId) {
        courseService.updateVideoOrders(videoOrder, courseId, courseTemporaryId);;
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
