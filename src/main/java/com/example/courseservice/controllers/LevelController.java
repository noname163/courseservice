package com.example.courseservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.dto.response.LevelResponse;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.levelservice.LevelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/levels")
public class LevelController {

    @Autowired
    private LevelService levelService;

    @Operation(summary = "Get all level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user information successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = LevelResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping()
    public ResponseEntity<List<LevelResponse>> getLevels() {
        return ResponseEntity.status(HttpStatus.OK).body(levelService.getListLevel());
    }
}
