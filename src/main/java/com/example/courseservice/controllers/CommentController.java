package com.example.courseservice.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.request.UpdateCommentRequest;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.commentservice.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Operation(summary = "Create comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create commentsuccessfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentRequest.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAnyAuthority('TEACHER','STUDENT')")
    @PostMapping("")
    public ResponseEntity<Void> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        commentService.createComment(commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Update comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update comment successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateCommentRequest.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAnyAuthority('TEACHER','STUDENT')")
    @PostMapping("/update")
    public ResponseEntity<Void> updateComment(@Valid @RequestBody UpdateCommentRequest commentRequest) {
        commentService.editContent(commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
