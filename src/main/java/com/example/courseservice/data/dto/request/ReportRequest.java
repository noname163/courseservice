package com.example.courseservice.data.dto.request;

import com.example.courseservice.data.constants.ReportType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReportRequest {
    private String content;
    private Long objectId;
    private ReportType reportType;
}
