package com.example.courseservice.services.dashboardservice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.dto.response.AdminDasboardResponse;
import com.example.courseservice.data.dto.response.TransactionByMonth;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.TransactionRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.services.dashboardservice.DashBoardService;
import com.example.courseservice.services.transactionservice.TransactionService;

@Service
public class DashBoardServiceImpl implements DashBoardService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TransactionService transactionService;

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

}
