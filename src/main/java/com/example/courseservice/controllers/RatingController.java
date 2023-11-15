package com.example.courseservice.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.RatingRequest;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.RatingResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.ratingservice.RatingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @Operation(summary = "Create rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create course rating."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAnyAuthority('STUDENT','TEACHER')")
    @PostMapping()
    public ResponseEntity<Void> createCourse(@Valid @RequestBody RatingRequest ratingRequest) {
        ratingService.createRating(ratingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get ratings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping()
    public ResponseEntity<PaginationResponse<List<RatingResponse>>> getCourses(
            @RequestParam(required = true) Long courseId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ratingService.getRatingByCourse(courseId, page, size, field, sortType));
    }
}
