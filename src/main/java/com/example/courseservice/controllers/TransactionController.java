package com.example.courseservice.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.TransactionStatus;
import com.example.courseservice.data.dto.request.AdminRefundAction;
import com.example.courseservice.data.dto.request.PaymentRequest;
import com.example.courseservice.data.dto.request.StudentRefundRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.data.dto.response.UserTransactionResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.transactionservice.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Operation(summary = "Create payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfull.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PostMapping()
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request) throws UnsupportedEncodingException {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                transactionService.createdPayment(paymentRequest, request));
    }

    @Operation(summary = "Get transaction of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get transaction successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserTransactionResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/user")
    public ResponseEntity<PaginationResponse<List<UserTransactionResponse>>> getTransactionOfCurrentUser(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionService.getTransactionOfCurrentUser(page, size, field, sortType));
    }

    @Operation(summary = "Admin get transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get transaction successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserTransactionResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<PaginationResponse<List<UserTransactionResponse>>> adminGetTransaction(
            @RequestParam(required = false, defaultValue = "") TransactionStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionService.getTransactionForAdmin(status, page, size, field, sortType));
    }

    @Operation(summary = "Admin Refund")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get transaction successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PostMapping("/admin/refund")
    public ResponseEntity<PaymentResponse> createRefund(@RequestBody AdminRefundAction adminRefundAction,
            HttpServletRequest request) throws UnsupportedEncodingException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(transactionService.adminHandleRefund(adminRefundAction, request));
    }

    @Operation(summary = "Student Request Refund")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get transaction successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PostMapping("/student/request")
    public ResponseEntity<Void> studentRequestRefund(@RequestBody StudentRefundRequest studentRefundRequest) {
        transactionService.requestRefund(studentRefundRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
