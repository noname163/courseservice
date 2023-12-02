package com.example.courseservice.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.response.AdminDasboardResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.dashboardservice.DashBoardService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.services.studentprogressservice.StudentProgressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {

    @Autowired
    private DashBoardService dashBoardService;

    @Operation(summary = "Get admin dashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get admin dashboard successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AdminDasboardResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public ResponseEntity<AdminDasboardResponse> getAdminDashBoard() {
        return ResponseEntity.status(HttpStatus.OK).body(dashBoardService.getAdminDasboardResponse());
    }

}
