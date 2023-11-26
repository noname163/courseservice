package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.ReportType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReportResponse {
    private Long reportId;
    private String userEmail;
    private String userName;
    private String userAvatar;
    private String userRole;
    private String objectName;
    private String reportContent;
    private ReportType reportType;
    private Boolean isProcessed;
    private LocalDateTime createDate;
}
