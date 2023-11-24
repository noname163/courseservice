package com.example.courseservice.services.teacherincomeservice;

import java.util.List;

import com.example.courseservice.data.dto.request.TeacherIncomeRequest;
import com.example.courseservice.data.dto.response.CourseRevenueByMonth;
import com.example.courseservice.data.dto.response.TeacherIncomeResponse;

public interface TeacherIncomeService {
    public void createTeacherIncome(TeacherIncomeRequest teacherIncomeRequest);

    public List<TeacherIncomeResponse>  getCurrentTeacherIncome();

    public List<TeacherIncomeResponse> getCurrentTeacherIncomeByCourseId(Long courseId);

    public List<CourseRevenueByMonth> getCurrentTeacherIncomeIn10Motnh();
}
