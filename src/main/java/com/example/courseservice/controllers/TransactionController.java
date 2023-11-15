package com.example.courseservice.controllers;

import com.example.courseservice.data.dto.request.PaymentRequest;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.event.EventPublisher;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.ForbiddenException;
import com.example.courseservice.services.transactionservice.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private EventPublisher eventPublisher;

    @Operation(summary = "Create payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "User not valid.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "401", description = "Username or password is incorrect. Please try again", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ForbiddenException.class))})
    })
    @PostMapping()
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            transactionService.createdPayment(paymentRequest,request)
        );
    }
}
