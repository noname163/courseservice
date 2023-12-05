package com.example.courseservice.data.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TeacherDashboardResponse {
    private Double monthlyIncome;
    private Long totalCourse;
    private Long totalVideo; 
    private Long totalStudent;
    private List<CourseRevenueByMonth> courseRevenueByMonths;
}
