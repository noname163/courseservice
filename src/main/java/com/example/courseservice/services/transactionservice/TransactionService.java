package com.example.courseservice.services.transactionservice;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import com.example.courseservice.data.dto.request.PaymentRequest;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.data.dto.response.TransactionResponse;

public interface TransactionService {
    public PaymentResponse createdPayment(PaymentRequest paymentRequest, HttpServletRequest request) throws UnsupportedEncodingException;
    public TransactionResponse checkPaymentStatus(String orderId, String responseCode,String transactionNo, String transDate) throws Exception;
}
