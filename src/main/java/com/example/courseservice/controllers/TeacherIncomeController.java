package com.example.courseservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.TeacherIncomeStatus;
import com.example.courseservice.data.dto.request.AdminPaymentTeacherRequest;
import com.example.courseservice.data.dto.response.CourseRevenueByMonth;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.TeacherIncomeForAdmin;
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
    public ResponseEntity<List<TeacherIncomeResponse>> getCourseRevenueById(
            @RequestParam(required = true) Long courseId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(teacherIncomeService.getCurrentTeacherIncomeByCourseId(courseId));
    }

    @Operation(summary = "Get current teacher income in 10 month of all course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get revenue successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherIncomeResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/course-revenue")
    public ResponseEntity<List<CourseRevenueByMonth>> getCurrentTeacherIncomeIn10Motnh() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(teacherIncomeService.getCurrentTeacherIncomeIn10Motnh());
    }

    @Operation(summary = "Get current teacher income in 10 month of all course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get revenue successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherIncomeResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/income")
    public ResponseEntity<PaginationResponse<List<TeacherIncomeForAdmin>>> getCurrentTeacherIncomeAdmin(
            @RequestParam(required = false, defaultValue = "ALL") TeacherIncomeStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(teacherIncomeService.getTeacherIncomeForTeacher(status, page, size, field, sortType));
    }
    @Operation(summary = "Get teacher income for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get revenue successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherIncomeForAdmin.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/income")
    public ResponseEntity<PaginationResponse<List<TeacherIncomeForAdmin>>> getCurrentTeacherIncomeAdminForAdmin(
            @RequestParam(required = false, defaultValue = "ALL") TeacherIncomeStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(teacherIncomeService.getTeacherIncomeForAdmin(status, page, size, field, sortType));
    }

    
    @Operation(summary = "Admmin payment for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get revenue successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid data.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/payment")
    public ResponseEntity<PaginationResponse<List<TeacherIncomeForAdmin>>> adminPaymentForTeacher(@RequestBody AdminPaymentTeacherRequest adminPaymentTeacherRequest) {
        teacherIncomeService.adminPaymentForTeacher(adminPaymentTeacherRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
