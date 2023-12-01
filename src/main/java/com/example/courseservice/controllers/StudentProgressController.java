package com.example.courseservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.studentprogressservice.StudentProgressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/student-progress")
public class StudentProgressController {
    @Autowired
    private StudentProgressService studentProgressService;

    @Operation(summary = "Student completed video ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid data.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/{videoId}")
    public ResponseEntity<Void> createStudentProgress(@PathVariable Long videoId){
        studentProgressService.createStudentProgressByVideoId(videoId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
