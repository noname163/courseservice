package com.example.courseservice.services.teacherincomeservice;

import java.util.List;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.TeacherIncomeStatus;
import com.example.courseservice.data.dto.request.TeacherIncomeRequest;
import com.example.courseservice.data.dto.response.CourseRevenueByMonth;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.TeacherIncomeForAdmin;
import com.example.courseservice.data.dto.response.TeacherIncomeResponse;

public interface TeacherIncomeService {
    public void createTeacherIncome(TeacherIncomeRequest teacherIncomeRequest);

    public List<TeacherIncomeResponse>  getCurrentTeacherIncome();

    public List<TeacherIncomeResponse> getCurrentTeacherIncomeByCourseId(Long courseId);

    public List<CourseRevenueByMonth> getCurrentTeacherIncomeIn10Motnh();

    public PaginationResponse<List<TeacherIncomeForAdmin>> getTeacherIncomeForAdmin(TeacherIncomeStatus status,Integer page,
            Integer size, String field, SortType sortType);
        
    public PaginationResponse<List<TeacherIncomeForAdmin>> getTeacherIncomeForTeacher(TeacherIncomeStatus status,Integer page,
            Integer size, String field, SortType sortType);
}
