package com.example.courseservice.services.reportservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.ReportType;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.ReportRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.ReportResponse;

public interface ReportService {
    public void createReport(ReportRequest reportRequest, MultipartFile multipartFile);

    public PaginationResponse<List<ReportResponse>> getListReportResponse(Integer page,
            Integer size, String field, SortType sortType);

    public PaginationResponse<List<ReportResponse>> getListReportResponseByStatus(ReportType reportType, Integer page,
            Integer size, String field, SortType sortType);

    public void processReport(VerifyRequest verifyRequest);
}
