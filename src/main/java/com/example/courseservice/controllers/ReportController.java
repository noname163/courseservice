package com.example.courseservice.controllers;

import java.util.List;

import javax.validation.Valid;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.ReportRequest;
import com.example.courseservice.data.dto.request.TopicEditRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.ReportResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;
import com.example.courseservice.services.reportservice.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @Operation(summary = "Create report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAnyAuthority('TEACHER','STUDENT')")
    @PostMapping()
    public ResponseEntity<Void> createReport(@Valid @RequestPart() ReportRequest reportRequest,
            @RequestPart(required = false) MultipartFile image) {
        reportService.createReport(reportRequest, image);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Process report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Process successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PatchMapping()
    public ResponseEntity<Void> processReport(@Valid @RequestBody VerifyRequest verifyRequest) {
        reportService.processReport(verifyRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Get list report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfull."),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public ResponseEntity<PaginationResponse<List<ReportResponse>>> getReportList(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reportService.getListReportResponse(page, size, field, sortType));
    }

}
