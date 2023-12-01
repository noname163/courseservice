package com.example.courseservice.services.transactionservice;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.TransactionStatus;
import com.example.courseservice.data.dto.request.AdminRefundAction;
import com.example.courseservice.data.dto.request.PaymentRequest;
import com.example.courseservice.data.dto.request.StudentRefundRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.data.dto.response.TransactionResponse;
import com.example.courseservice.data.dto.response.UserTransactionResponse;

public interface TransactionService {
    public PaymentResponse createdPayment(PaymentRequest paymentRequest, HttpServletRequest request)
            throws UnsupportedEncodingException;

    public TransactionResponse checkPaymentStatus(String orderId, String responseCode, String transactionNo,
            String transDate) throws Exception;

    public PaginationResponse<List<UserTransactionResponse>> getTransactionOfCurrentUser(Integer page,
            Integer size, String field, SortType sortType);

    public PaginationResponse<List<UserTransactionResponse>> getTransactionOfCurrentUserForTeacher(Integer page,
            Integer size, String field, SortType sortType);

    public PaginationResponse<List<UserTransactionResponse>> getTransactionForAdmin(TransactionStatus transactionStatus,
            Integer page,
            Integer size, String field, SortType sortType);

    public void requestRefund(StudentRefundRequest studentRefundRequest);

    public void adminHandleRefund(AdminRefundAction adminRefundAction, HttpServletRequest request)throws UnsupportedEncodingException;
}
