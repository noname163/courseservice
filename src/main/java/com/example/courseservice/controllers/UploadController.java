package com.example.courseservice.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.uploadservice.UploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Autowired
    private FileService fileService;
    @Autowired
    private UploadService uploadService;

    @Operation(summary = "Upload Video")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Accepted."),
        @ApiResponse(responseCode = "400", description = "File not valid.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))
        })
    })
    @PostMapping("/video")
    public ResponseEntity<Void> uploadVideo(@RequestPart("file") MultipartFile multipartFile) throws IOException {
        // uploadService.splitAndUploadVideo(multipartFile);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

