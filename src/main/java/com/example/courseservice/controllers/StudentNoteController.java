package com.example.courseservice.controllers;

import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.dto.request.StudentNoteEdit;
import com.example.courseservice.data.dto.request.StudentNoteRequest;
import com.example.courseservice.data.dto.response.StudentNoteResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.studentnoteservice.StudentNoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/student-notes")
@PreAuthorize("hasAuthority('STUDENT')")
public class StudentNoteController {
    @Autowired
    private StudentNoteService studentNoteService;

    @Operation(summary = "Create note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StudentNoteRequest.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PostMapping()
    public ResponseEntity<StudentNoteResponse> createReport(@Valid @RequestBody StudentNoteRequest studentNoteRequest) {

        return ResponseEntity.status(HttpStatus.OK).body(studentNoteService.createNote(studentNoteRequest));
    }

    @Operation(summary = "Edit note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StudentNoteRequest.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PutMapping()
    public ResponseEntity<StudentNoteResponse> createReport(@Valid @RequestBody StudentNoteEdit studentNoteRequest) {

        return ResponseEntity.status(HttpStatus.OK).body(studentNoteService.editStudentNote(studentNoteRequest));
    }

    @Operation(summary = "Get note of current user by videoId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StudentNoteRequest.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping()
    public ResponseEntity<List<StudentNoteResponse>> getNotes(@RequestParam(required = true) Long videoId) {

        return ResponseEntity.status(HttpStatus.OK).body(studentNoteService.getNoteByVideoId(videoId));
    }

    @Operation(summary = "Delete note current user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @DeleteMapping()
    public ResponseEntity<Void> deleteNote(@RequestParam(required = true) Long id) {
        studentNoteService.deleteNote(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
