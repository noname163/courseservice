package com.example.courseservice.data.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VnPayConstants {
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_TRANSACTION_TYPE = "02";
    public static final String VNP_REFUND_COMMAND = "refund";
    public static final String VNP_PAYURL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_API_URL = "https://sandbox.vnpayment.vn/merchantv2/";
}
