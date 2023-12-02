package com.example.courseservice.services.transactionservice.impl;

import java.io.ObjectInputFilter.Config;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.courseservice.configs.VNPayConfig;
import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.TransactionStatus;
import com.example.courseservice.data.constants.Validation;
import com.example.courseservice.data.constants.VerifyStatus;
import com.example.courseservice.data.constants.VnPayConstants;
import com.example.courseservice.data.dto.request.AdminRefundAction;
import com.example.courseservice.data.dto.request.PaymentRequest;
import com.example.courseservice.data.dto.request.SendMailRequest;
import com.example.courseservice.data.dto.request.StudentEnrollRequest;
import com.example.courseservice.data.dto.request.StudentRefundRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.data.dto.response.TransactionResponse;
import com.example.courseservice.data.dto.response.UserTransactionResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Notification;
import com.example.courseservice.data.entities.Transaction;
import com.example.courseservice.data.object.NotificationContent;
import com.example.courseservice.data.object.TransactionResponseInterface;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.TransactionRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.TeacherIncomeMapper;
import com.example.courseservice.mappers.TransactionMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.notificationservice.NotificationService;
import com.example.courseservice.services.sendmailservice.SendEmailService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.services.teacherincomeservice.TeacherIncomeService;
import com.example.courseservice.services.transactionservice.TransactionService;
import com.example.courseservice.template.SendMailTemplate;
import com.example.courseservice.utils.ConvertStringToLocalDateTime;
import com.example.courseservice.utils.EnvironmentVariable;
import com.example.courseservice.utils.GetIpAddress;
import com.example.courseservice.utils.PageableUtil;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private StudentEnrollCourseService studentEnrollCourseService;
    @Autowired
    private TeacherIncomeService teacherIncomeService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TeacherIncomeMapper teacherIncomeMapper;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private ConvertStringToLocalDateTime convertStringToLocalDateTime;
    @Autowired
    private GetIpAddress getIpAddress;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private EnvironmentVariable environmentVariable;
    @Autowired
    private PageableUtil pageableUtil;

    @Transactional
    @Override
    public PaymentResponse createdPayment(PaymentRequest paymentRequest, HttpServletRequest request)
            throws UnsupportedEncodingException {
        UserInformation user = securityContextService.getCurrentUser();
        if (studentEnrollCourseService.isStudentEnrolled(user.getEmail(), paymentRequest.getCourseId())) {
            throw new BadRequestException("User have buy this course");
        }
        Course course = courseRepository.findById(paymentRequest.getCourseId())
                .orElseThrow(() -> new BadRequestException(
                        "Not exist course with id " + paymentRequest.getCourseId()));
        String orderType = course.getName();
        double amount = (double) (course.getPrice() * 100);
        String bankCode = paymentRequest.getBankCode();

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_TmnCode = environmentVariable.getVnpayTmnCode();

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

        String vnp_createdDate = currentTime.format(localDateFormat);
        vnp_Params.put("vnp_createdDate", vnp_createdDate);

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
        String vnp_SecureHash = VNPayConfig.hmacSHA512(environmentVariable.getVnPayHashSecret(),
                hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConstants.VNP_PAYURL + "?" + queryUrl;
        // set transaction
        LocalDateTime createdDate = convertStringToLocalDateTime.convertStringToLocalDateTime(vnp_createdDate);
        LocalDateTime expireDate = convertStringToLocalDateTime.convertStringToLocalDateTime(vnp_ExpireDate);

        Transaction transaction = Transaction
                .builder()
                .createdDate(createdDate)
                .userEmail(user.getEmail())
                .userName(user.getFullname())
                .userAvatar(user.getAvatar())
                .course(course)
                .amount(amount)
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
                .orElseThrow(() -> new BadRequestException(
                        "Not exist transaction with id " + vnp_TxnRef));

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
        Course course = transaction.getCourse();
        if (transactionStatus.equals(TransactionStatus.SUCCESS)) {
            studentEnrollCourseService.insertStudentEnroll(StudentEnrollRequest
                    .builder()
                    .courseId(course.getId())
                    .email(transaction.getUserEmail())
                    .studentId(transaction.getUserId())
                    .build());
            teacherIncomeService
                    .createTeacherIncome(teacherIncomeMapper
                            .mapTransactionToTeacherIncomeRequest(transaction));

            notificationService
                    .sendNotification(notificationService
                            .createNotificationForCurrentUser(NotificationContent
                                    .builder()
                                    .course(course.getName())
                                    .price(course.getPrice())
                                    .email(transaction.getUserEmail())
                                    .userId(transaction.getUserId())
                                    .type(NotificationType.TRANSACTION)
                                    .date(transLocalDate)
                                    .build()));
        }

        sendEmailService.sendMailService(SendMailRequest
                .builder()
                .subject("Thanh Toán Thành Công")
                .mailTemplate(SendMailTemplate
                        .paymentSuccessEmail(transaction.getUserEmail(), course.getName(),
                                course.getPrice()))
                .userEmail(transaction.getUserEmail())
                .build());
        return transactionMapper.mapEntityToDto(transaction);
    }

    @Override
    public PaginationResponse<List<UserTransactionResponse>> getTransactionOfCurrentUser(
            TransactionStatus transactionStatus, Integer page, Integer size,
            String field,
            SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Long userId = securityContextService.getCurrentUser().getId();
        Page<TransactionResponseInterface> userTransaction;
        if (transactionStatus.equals(TransactionStatus.ALL)) {
            userTransaction = transactionRepository.findTransactionsByUserId(userId,
                    pageable);
        } else {
            userTransaction = transactionRepository.findTransactionsByUserIdAndStatus(userId, transactionStatus,
                    pageable);
        }
        return PaginationResponse.<List<UserTransactionResponse>>builder()
                .data(transactionMapper.mapToTransactionResponseList(userTransaction.getContent()))
                .totalPage(userTransaction.getTotalPages())
                .totalRow(userTransaction.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<UserTransactionResponse>> getTransactionForAdmin(
            TransactionStatus transactionStatus,
            Integer page, Integer size, String field,
            SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<TransactionResponseInterface> userTransaction = transactionRepository
                .findTransactionsByStatus(transactionStatus, pageable);
        return PaginationResponse.<List<UserTransactionResponse>>builder()
                .data(transactionMapper.mapToTransactionResponseList(userTransaction.getContent()))
                .totalPage(userTransaction.getTotalPages())
                .totalRow(userTransaction.getTotalElements())
                .build();
    }

    @Override
    public void requestRefund(StudentRefundRequest studentRefundRequest) {
        Transaction transaction = transactionRepository.findById(studentRefundRequest.getId()).orElseThrow(
                () -> new BadRequestException(
                        "Not found transaction with id " + studentRefundRequest.getId()));
        Course course = transaction.getCourse();
        UserInformation userInformation = securityContextService.getCurrentUser();
        if (transaction.getUserId() != userInformation.getId()) {
            throw new InValidAuthorizationException("Require owner permisson");
        }
        if (ChronoUnit.DAYS.between(transaction.getPaymentDate(), LocalDateTime.now()) > 3) {
            throw new BadRequestException("Request not accepted because it over 3 date");
        }
        transaction.setStatus(TransactionStatus.REFUND);
        transaction.setStudentNote(studentRefundRequest.getReason());
        transactionRepository.save(transaction);

        notificationService
                .sendNotification(
                        notificationService.createNotificationForCurrentUser(NotificationContent
                                .builder()
                                .course(course.getName())
                                .price(course.getPrice())
                                .email(transaction.getUserEmail())
                                .userId(transaction.getUserId())
                                .type(NotificationType.TRANSACTION)
                                .date(LocalDateTime.now())
                                .build()));
        sendEmailService.sendMailService(SendMailRequest
                .builder().subject("Gửi Yêu Cầu Thành Công")
                .mailTemplate(
                        SendMailTemplate.recivedRefundRequestEmail(
                                userInformation.getFullname(), course.getName(),
                                transaction.getVnpTxnRef()))
                .userEmail(transaction.getUserEmail()).build());
    }

    @Override
    public void adminHandleRefund(AdminRefundAction adminRefundAction, HttpServletRequest request)
            throws UnsupportedEncodingException {
        Transaction transaction = transactionRepository.findById(adminRefundAction.getId()).orElseThrow(
                () -> new BadRequestException(
                        "Cannot found transaction with id " + adminRefundAction.getId()));
        Course course = transaction.getCourse();
        String sendMailTemplate = "";
        if (adminRefundAction.getVerifyStatus().equals(VerifyStatus.ACCEPTED)) {
            transaction.setRefundEvidence(adminRefundAction.getTransactionCode());
            transaction.setStatus(TransactionStatus.REFUND_SUCCES);
            sendMailTemplate = SendMailTemplate.acceptedRefundEmail(transaction.getUserEmail(),
                    course.getName(),
                    adminRefundAction.getTransactionCode());
        } else {
            transaction.setAdminNote(adminRefundAction.getReason());
            transaction.setStatus(TransactionStatus.REJECT_REFUND);
            sendMailTemplate = SendMailTemplate.rejectRefundEmail(transaction.getUserEmail(),
                    course.getName(),
                    transaction.getVnpTxnRef(), adminRefundAction.getReason());
        }
        transactionRepository.save(transaction);

        notificationService.createNotification(NotificationContent
                .builder()
                .course(course.getName())
                .price(course.getPrice())
                .email(transaction.getUserEmail())
                .userId(transaction.getUserId())
                .type(NotificationType.TRANSACTION)
                .date(LocalDateTime.now())
                .build());
        sendEmailService.sendMailService(SendMailRequest
                .builder().subject("Thông báo trạng thái hoàn tiền")
                .mailTemplate(sendMailTemplate)
                .userEmail(transaction.getUserEmail()).build());
    }

    private PaymentResponse refundPayment(Transaction transaction, HttpServletRequest request)
            throws UnsupportedEncodingException {

        String vnp_Reuqest_id = VNPayConfig.getRandomNumber(8);
        String vnp_TmnCode = environmentVariable.getVnpayTmnCode();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnPayConstants.VNP_VERSION);
        vnp_Params.put("vnp_Command", VnPayConstants.VNP_REFUND_COMMAND);
        vnp_Params.put("vnp_TransactionType", VnPayConstants.VNP_TRANSACTION_TYPE);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_RequestId", vnp_Reuqest_id);
        vnp_Params.put("vnp_Amount", String.valueOf(transaction.getAmount()));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", transaction.getVnpTxnRef());
        vnp_Params.put("vnp_OrderInfo", "Hoàn trả don hang:" + transaction.getVnpTxnRef());

        String vnp_IpAddr = getIpAddress.getClientIpAddress(request);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern(Validation.DATE_TIME_FORMAT);

        String vnp_createdDate = currentTime.format(localDateFormat);
        vnp_Params.put("vnp_createdDate", vnp_createdDate);

        String vnp_ExpireDate = currentTime.plusMinutes(15l).format(localDateFormat);
        vnp_Params.put("vnp_TransactionDate", vnp_createdDate);

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
        String vnp_SecureHash = VNPayConfig.hmacSHA512(environmentVariable.getVnPayHashSecret(),
                hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConstants.VNP_PAYURL + "?" + queryUrl;
        // set transaction
        LocalDateTime createdDate = convertStringToLocalDateTime.convertStringToLocalDateTime(vnp_createdDate);
        LocalDateTime expireDate = convertStringToLocalDateTime.convertStringToLocalDateTime(vnp_ExpireDate);

        PaymentResponse paymentResponse = PaymentResponse
                .builder()
                .code("200")
                .message("success")
                .data(paymentUrl)
                .build();
        return paymentResponse;
    }

    @Override
    public PaginationResponse<List<UserTransactionResponse>> getTransactionOfCurrentUserForTeacher(TransactionStatus status,Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Long userId = securityContextService.getCurrentUser().getId();
        Page<TransactionResponseInterface> userTransaction;
        if(status.equals(TransactionStatus.ALL)){
            userTransaction = transactionRepository.getTransactionsByTeacherId(
                userId,
                pageable);
        }else{
            userTransaction = transactionRepository.getTransactionsByTeacherIdAndStatus(userId, status, pageable);
        }
        return PaginationResponse.<List<UserTransactionResponse>>builder()
                .data(transactionMapper.mapToTransactionResponseList(userTransaction.getContent()))
                .totalPage(userTransaction.getTotalPages())
                .totalRow(userTransaction.getTotalElements())
                .build();
    }
}
