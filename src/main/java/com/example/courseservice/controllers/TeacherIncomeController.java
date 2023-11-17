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

import com.example.courseservice.data.dto.request.ReactRequest;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.data.dto.response.TeacherIncomeResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.teacherincomeservice.TeacherIncomeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/teacher-incomes")
public class TeacherIncomeController {
    @Autowired
    private TeacherIncomeService teacherIncomeService;

    @Operation(summary = "Get current teacher income")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create react successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherIncomeResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping()
    public ResponseEntity<List<TeacherIncomeResponse>> getCurrentTeacherIncome() {
        return ResponseEntity.status(HttpStatus.OK).body(teacherIncomeService.getCurrentTeacherIncome());
    }

    @Operation(summary = "Get current teacher income by course id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create react successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherIncomeResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/by-course")
    public ResponseEntity<List<TeacherIncomeResponse>> reactVideo(
            @RequestParam(required = true) Long courseId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(teacherIncomeService.getCurrentTeacherIncomeByCourseId(courseId));
    }
}
