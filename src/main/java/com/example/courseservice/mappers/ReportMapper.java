package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.ReportResponse;
import com.example.courseservice.data.object.ReportResponseInterface;

@Component
public class ReportMapper {
    public ReportResponse mapToReportResponse(ReportResponseInterface reportResponseInterface) {
        if (reportResponseInterface == null) {
            return null;
        }

        return ReportResponse.builder()
                .id(reportResponseInterface.getReportId())
                .userName(reportResponseInterface.getUserName())
                .userAvatar(reportResponseInterface.getUserAvatar())
                .userRole(reportResponseInterface.getUserRole())
                .objectName(reportResponseInterface.getObjectName())
                .reportContent(reportResponseInterface.getReportContent())
                .reportType(reportResponseInterface.getReportType())
                .isProcessed(reportResponseInterface.getIsProcessed())
                .createdDate(reportResponseInterface.getcreatedDate())
                .objectId(reportResponseInterface.getObjectId())
                .build();
    }

    public List<ReportResponse> mapToReportResponseList(List<ReportResponseInterface> reportResponseInterfaces) {
        return reportResponseInterfaces.stream()
                .map(this::mapToReportResponse)
                .collect(Collectors.toList());
    }
}
