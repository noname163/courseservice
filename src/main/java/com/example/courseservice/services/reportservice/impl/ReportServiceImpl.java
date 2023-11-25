package com.example.courseservice.services.reportservice.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.NotificationTitle;
import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.constants.ReportType;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.ReportRequest;
import com.example.courseservice.data.dto.request.SendMailRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.NotificationResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.ReportResponse;
import com.example.courseservice.data.entities.Comment;
import com.example.courseservice.data.entities.ReplyComment;
import com.example.courseservice.data.entities.Report;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.NotificationContent;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CommentRepository;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.ReplyCommentRepository;
import com.example.courseservice.data.repositories.ReportRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.ReportMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.notificationservice.NotificationService;
import com.example.courseservice.services.reportservice.ReportService;
import com.example.courseservice.services.sendmailservice.SendEmailService;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.template.SendMailTemplate;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ReplyCommentRepository replyCommentRepository;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private ReportMapper reportMapper;

    @Override
    public void createReport(ReportRequest reportRequest, MultipartFile multipartFile) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        boolean flag = true;
        Report report = Report
                .builder()
                .userEmail(currentUser.getEmail())
                .userId(currentUser.getId())
                .createDate(LocalDateTime.now())
                .message(reportRequest.getContent())
                .reportType(reportRequest.getReportType())
                .objectId(reportRequest.getObjectId())
                .build();
        switch (reportRequest.getReportType()) {
            case COMMENT:
                flag = commentRepository.existsById(reportRequest.getObjectId());
                break;
            case VIDEO:
                flag = videoRepository.existsById(reportRequest.getObjectId());
            case COURSE:
                flag = courseRepository.existsById(reportRequest.getObjectId());
                break;
            case REPLY:
                flag = replyCommentRepository.existsById(reportRequest.getObjectId());
                break;
            default:
                throw new BadRequestException("Must have report type");
        }
        if (!flag) {
            throw new BadRequestException("Cannot found object with id " + reportRequest.getObjectId() + " of type "
                    + reportRequest.getReportType().toString());
        }
        if(multipartFile != null){
            CloudinaryUrl cloudinaryUrl = uploadService.uploadMedia(multipartFile);
            report.setUrl(cloudinaryUrl.getUrl());
        }
        reportRepository.save(report);

        NotificationResponse notificationContent = notificationService.createNotificationForCurrentUser(NotificationContent
                .builder()
                .title(NotificationTitle.SYSTEM_TITLE)
                .content("Hệ thống đã ghi nhận báo cáo của bạn")
                .email(currentUser.getEmail())
                .userId(currentUser.getId())
                .type(NotificationType.SYSTEM)
                .build());
        notificationService.sendNotification(notificationContent);
        
        sendEmailService.sendMailService(SendMailRequest
                .builder()
                .subject("Thông báo hệ thống")
                .mailTemplate(SendMailTemplate.reportSuccessEmail(currentUser.getFullname()))
                .userEmail(currentUser.getEmail())
                .build());
    }

    @Override
    public PaginationResponse<List<ReportResponse>> getListReportResponse(Integer page, Integer size, String field,
            SortType sortType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getListReportResponse'");
    }

}
