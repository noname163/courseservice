package com.example.courseservice.services.reportservice.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.NotificationTitle;
import com.example.courseservice.data.constants.NotificationType;
import com.example.courseservice.data.constants.ReportType;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.VerifyStatus;
import com.example.courseservice.data.dto.request.ReportRequest;
import com.example.courseservice.data.dto.request.SendMailRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.NotificationResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.ReportResponse;
import com.example.courseservice.data.entities.Comment;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.ReplyComment;
import com.example.courseservice.data.entities.Report;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.NotificationContent;
import com.example.courseservice.data.object.ReportResponseInterface;
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
import com.example.courseservice.utils.PageableUtil;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private VideoRepository videoRepository;
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
    @Autowired
    private PageableUtil pageableUtil;

    @Override
    public void createReport(ReportRequest reportRequest, MultipartFile multipartFile) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        videoRepository.findById(reportRequest.getObjectId())
                .orElseThrow(() -> new BadRequestException(
                        "Not found video with id " + reportRequest.getObjectId()));

        Report report = Report
                .builder()
                .userAvatar(currentUser.getAvatar())
                .userRole(currentUser.getRole())
                .userName(currentUser.getFullname())
                .userEmail(currentUser.getEmail())
                .userId(currentUser.getId())
                .isProcessed(false)
                .createDate(LocalDateTime.now())
                .message(reportRequest.getContent())
                .reportType(reportRequest.getReportType())
                .objectId(reportRequest.getObjectId())
                .build();

        if (multipartFile != null) {
            CloudinaryUrl cloudinaryUrl = uploadService.uploadMedia(multipartFile);
            report.setUrl(cloudinaryUrl.getUrl());
        }

        reportRepository.save(report);

        NotificationResponse notificationContent = notificationService
                .createNotificationForCurrentUser(NotificationContent
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
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<ReportResponseInterface> reportResponseInterfaces = reportRepository
                .getReportResponsesForVideos(pageable);

        return PaginationResponse.<List<ReportResponse>>builder()
                .data(reportMapper.mapToReportResponseList(reportResponseInterfaces.getContent()))
                .totalPage(reportResponseInterfaces.getTotalPages())
                .totalRow(reportResponseInterfaces.getTotalElements())
                .build();
    }

    @Override
    public void processReport(VerifyRequest verifyRequest) {
        Report report = reportRepository.findById(verifyRequest.getId())
                .orElseThrow(() -> new BadRequestException("Not found report with id " + verifyRequest.getId()));
        Video video = videoRepository.findById(report.getObjectId())
                .orElseThrow(() -> new BadRequestException("Not found video with id " + report.getObjectId()));
        Course course = video.getCourse();
        String notificationContent = "Hệ thống từ chối báo cáo video tên " + video.getName() + " của bạn. Lý do "
                + verifyRequest.getReason();
        report.setIsProcessed(true);
        if (verifyRequest.getVerifyStatus().equals(VerifyStatus.ACCEPTED)) {
            video.setStatus(CommonStatus.BANNED);
            videoRepository.save(video);
            sendEmailService.sendMailService(SendMailRequest
                    .builder()
                    .subject("Thông báo hệ thống")
                    .mailTemplate(SendMailTemplate.acceptReportEmail(report.getUserName(), report.getMessage()))
                    .userEmail(report.getUserEmail())
                    .build());
            sendEmailService.sendMailService(SendMailRequest
                    .builder()
                    .subject("Thông báo hệ thống")
                    .mailTemplate(SendMailTemplate.reportMessageToTeacherEmail(course.getTeacherName(), video.getName(),
                            course.getName(), verifyRequest.getReason()))
                    .userEmail(course.getTeacherEmail())
                    .build());
            notificationContent = "Hệ thống chấp nhận báo cáo video " + video.getName() + " của bạn.";
        }
        reportRepository.save(report);
        notificationService
                .createNotification(NotificationContent
                        .builder()
                        .title(NotificationTitle.SYSTEM_TITLE)
                        .content(notificationContent)
                        .email(report.getUserEmail())
                        .userId(report.getUserId())
                        .type(NotificationType.SYSTEM)
                        .build());
    }

}
