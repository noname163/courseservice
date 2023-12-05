package com.example.courseservice.services.dashboardservice;

import com.example.courseservice.data.dto.response.AdminDasboardResponse;
import com.example.courseservice.data.dto.response.TeacherDashboardResponse;

public interface DashBoardService {
    public AdminDasboardResponse getAdminDasboardResponse();
    public TeacherDashboardResponse getTeacherDashboardResponse();
}
