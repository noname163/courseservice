package com.example.courseservice.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.dto.request.ReactRequest;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.reactvideoservice.ReactVideoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/react")
public class ReactVideoController {
    @Autowired
    private ReactVideoService reactVideoService;

    @Operation(summary = "Create react")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create react successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))})
    })
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping()
    public ResponseEntity<Void> reactVideo(@RequestBody @Valid ReactRequest reactRequest){
        reactVideoService.createReact(reactRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
