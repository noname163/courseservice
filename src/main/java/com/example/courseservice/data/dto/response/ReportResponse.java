package com.example.courseservice.data.dto.response;

import com.example.courseservice.data.constants.ReportType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReportResponse {
    private Long reportId;
    private String userEmail;
    private Long userId;
    private String reportContent;
    private ReportType reportType;
}
