package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.ReportType;

public interface ReportResponseInterface {
    Long getReportId();

    Long getObjectId();

    String getUserName();

    String getUserAvatar();

    String getUserRole();

    String getObjectName();

    String getReportContent();

    ReportType getReportType();

    LocalDateTime getcreatedDate();

    Boolean getIsProcessed();
}
