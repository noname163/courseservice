package com.example.courseservice.mappers;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.ReportResponse;
import com.example.courseservice.data.entities.Report;

@Component
public class ReportMapper {
    public ReportResponse mapToReportResponse(Report report) {
        return ReportResponse
                .builder()
                .reportContent(report.getMessage())
                .userEmail(report.getUserEmail())
                .reportId(report.getId())
                .userId(report.getUserId())
                .reportType(report.getReportType())
                .build();
    }
}
