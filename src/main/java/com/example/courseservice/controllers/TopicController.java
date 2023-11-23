package com.example.courseservice.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.dto.request.TopicEditRequest;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/course-topic")
public class TopicController {
    @Autowired
    private CourseTopicService courseTopicService;

    @Operation(summary = "Add topic for course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/add-topic-course")
    public ResponseEntity<Void> addTopicForCourse(@RequestBody @Valid TopicEditRequest topicEditRequest){
        courseTopicService.addTopicByCourseId(topicEditRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Add topic for temporary course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/add-topic-waiting-course")
    public ResponseEntity<Void> addTopicForTmpCourse(@RequestBody @Valid TopicEditRequest topicEditRequest){
        courseTopicService.addTopicByCourseTmpId(topicEditRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Remove topic for temporary course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/remove-topic-waiting-course")
    public ResponseEntity<Void> removeTopicForTmpCourse(@RequestBody @Valid TopicEditRequest topicEditRequest){
        courseTopicService.removeTopicByCourseTmpId(topicEditRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Remove topic for course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/remove-topic-course")
    public ResponseEntity<Void> removeTopicForCourse(@RequestBody @Valid TopicEditRequest topicEditRequest){
        courseTopicService.removeTopicByCourseId(topicEditRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
}
