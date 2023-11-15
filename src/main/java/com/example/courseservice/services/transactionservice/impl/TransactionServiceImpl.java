package com.example.courseservice.services.transactionservice.impl;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.configs.VNPayConfig;
import com.example.courseservice.data.constants.TransactionStatus;
import com.example.courseservice.data.constants.Validation;
import com.example.courseservice.data.constants.VnPayConstants;
import com.example.courseservice.data.dto.request.PaymentRequest;
import com.example.courseservice.data.dto.request.StudentEnrollRequest;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.data.dto.response.TransactionResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Transaction;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.TransactionRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.TransactionMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.services.transactionservice.TransactionService;
import com.example.courseservice.utils.ConvertStringToLocalDateTime;
import com.example.courseservice.utils.EnvironmentVariable;
import com.example.courseservice.utils.GetIpAddress;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private StudentEnrollCourseService studentEnrollCourseService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private ConvertStringToLocalDateTime convertStringToLocalDateTime;
    @Autowired
    private GetIpAddress getIpAddress;
    @Autowired
    private EnvironmentVariable environmentVariable;

    @Transactional
    @Override
    public PaymentResponse createdPayment(PaymentRequest paymentRequest, HttpServletRequest request)
            throws UnsupportedEncodingException {
        UserInformation user = securityContextService.getCurrentUser();
        if (studentEnrollCourseService.isStudentEnrolled(user.getEmail(), paymentRequest.getCourseId())) {
            throw new BadRequestException("User have buy this course");
        }
        Course course = courseRepository.findById(paymentRequest.getCourseId())
                .orElseThrow(() -> new BadRequestException("Not exist course with id " + paymentRequest.getCourseId()));
        String orderType = course.getName();
        long amount = (long) (course.getPrice() * 100);
        String bankCode = paymentRequest.getBankCode();

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_TmnCode = VnPayConstants.VNP_TMNCODE;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnPayConstants.VNP_VERSION);
        vnp_Params.put("vnp_Command", VnPayConstants.VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = paymentRequest.getLanguage();
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        String vpn_ReturnUrl = environmentVariable.getVnPayReturnURL();
        vnp_Params.put("vnp_ReturnUrl", vpn_ReturnUrl);
        String vnp_IpAddr = getIpAddress.getClientIpAddress(request);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern(Validation.DATE_TIME_FORMAT);

        String vnp_CreateDate = currentTime.format(localDateFormat);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        String vnp_ExpireDate = currentTime.plusMinutes(15l).format(localDateFormat);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VnPayConstants.VNP_HASHSECRET, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConstants.VNP_PAYURL + "?" + queryUrl;
        // set transaction
        LocalDateTime createdDate = convertStringToLocalDateTime.convertStringToLocalDateTime(vnp_CreateDate);
        LocalDateTime expireDate = convertStringToLocalDateTime.convertStringToLocalDateTime(vnp_ExpireDate);

        Transaction transaction = Transaction
                .builder()
                .createDate(createdDate)
                .userEmail(user.getEmail())
                .course(course)
                .userId(user.getId())
                .vnpTxnRef(vnp_TxnRef)
                .status(TransactionStatus.PENDING)
                .expriedDate(expireDate)
                .build();

        PaymentResponse paymentResponse = PaymentResponse
                .builder()
                .code("200")
                .message("success")
                .data(paymentUrl)
                .build();
        transactionRepository.save(transaction);

        return paymentResponse;
    }

    @Transactional
    @Override
    public TransactionResponse checkPaymentStatus(String vnp_TxnRef, String responeCode, String transactionNo,
            String transDate) throws Exception {

        Transaction transaction = transactionRepository.findByVnpTxnRef(vnp_TxnRef)
                .orElseThrow(() -> new BadRequestException("Not exist transaction with id " + vnp_TxnRef));

        String vnp_TransDate = transDate;
        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;

        if (!responeCode.equals("00")) {
            transactionStatus = TransactionStatus.FAIL;
        }

        URL url = new URL(VnPayConstants.VNP_API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        LocalDateTime transLocalDate = convertStringToLocalDateTime.convertStringToLocalDateTime(vnp_TransDate);

        transaction.setPaymentDate(transLocalDate);
        transaction.setStatus(transactionStatus);
        transactionRepository.save(transaction);

        if (transactionStatus.equals(TransactionStatus.SUCCESS)) {
            studentEnrollCourseService.insertStudentEnroll(StudentEnrollRequest
                    .builder()
                    .courseId(transaction.getCourse().getId())
                    .email(transaction.getUserEmail())
                    .studentId(transaction.getUserId())
                    .build());
        }

        return transactionMapper.mapEntityToDto(transaction);
    }

}
