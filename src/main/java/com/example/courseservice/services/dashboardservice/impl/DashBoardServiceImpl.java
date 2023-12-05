package com.example.courseservice.services.dashboardservice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.TransactionStatus;
import com.example.courseservice.data.dto.response.AdminDasboardResponse;
import com.example.courseservice.data.dto.response.CourseRevenueByMonth;
import com.example.courseservice.data.dto.response.TeacherDashboardResponse;
import com.example.courseservice.data.dto.response.TransactionByMonth;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.StudentEnrolledCoursesRepository;
import com.example.courseservice.data.repositories.TeacherIncomeRepository;
import com.example.courseservice.data.repositories.TransactionRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.mappers.TeacherIncomeMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.dashboardservice.DashBoardService;
import com.example.courseservice.services.teacherincomeservice.TeacherIncomeService;
import com.example.courseservice.services.transactionservice.TransactionService;

@Service
public class DashBoardServiceImpl implements DashBoardService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TeacherIncomeRepository teacherIncomeRepository;
    @Autowired
    private TeacherIncomeService teacherIncomeService;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private TeacherIncomeMapper teacherIncomeMapper;

    @Override
    public AdminDasboardResponse getAdminDasboardResponse() {
        Long totalVideo = videoRepository.count();
        Long totalCourse = courseRepository.count();
        Double monthlyIncome = transactionService.getTotalInComeByMonth();
        List<TransactionByMonth> transactionByMonths = transactionService.getTransactionByMonths();
        return AdminDasboardResponse
                .builder()
                .monthlyIncome(monthlyIncome)
                .totalCourse(totalCourse)
                .totalVideo(totalVideo)
                .transactionByMonths(transactionByMonths)
                .build();
    }

    @Override
    public TeacherDashboardResponse getTeacherDashboardResponse() {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Long totalVideo = videoRepository.countVideosByTeacherId(currentUser.getId());
        Long totalCourse = courseRepository.countByTeacherId(currentUser.getId());
        Long totalStudent = courseRepository.countStudentsEnrolledByTeacherId(currentUser.getId());
        Double monthlyIncome = teacherIncomeRepository.getTotalIncomeByUserIdAndCurrentMonth(currentUser.getId());
        List<CourseRevenueByMonth> courseRevenueByMonths = teacherIncomeService.getCurrentTeacherIncomeIn10Motnh();
        return TeacherDashboardResponse
                .builder()
                .courseRevenueByMonths(courseRevenueByMonths)
                .monthlyIncome(monthlyIncome)
                .totalCourse(totalCourse)
                .totalStudent(totalStudent)
                .totalVideo(totalVideo)
                .build();
    }

}
