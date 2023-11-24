package com.example.courseservice.services.teacherincomeservice.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.TeacherIncomeStatus;
import com.example.courseservice.data.dto.request.TeacherIncomeRequest;
import com.example.courseservice.data.dto.response.CourseRevenueByMonth;
import com.example.courseservice.data.dto.response.TeacherIncomeResponse;
import com.example.courseservice.data.entities.TeacherIncome;
import com.example.courseservice.data.object.CourseReportInterface;
import com.example.courseservice.data.object.CourseRevenueByMonthInterface;
import com.example.courseservice.data.repositories.TeacherIncomeRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.TeacherIncomeMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.teacherincomeservice.TeacherIncomeService;

@Service
public class TeacherIncomeServiceImpl implements TeacherIncomeService {
    @Autowired
    private TeacherIncomeRepository teacherIncomeRepository;
    @Autowired
    private TeacherIncomeMapper teacherIncomeMapper;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public void createTeacherIncome(TeacherIncomeRequest teacherIncomeRequest) {
        if (Optional.of(teacherIncomeRequest.getAmount()).isEmpty()
                || Optional.of(teacherIncomeRequest.getCourse()).isEmpty()) {
            throw new BadRequestException("Invalid information provide for Income service");
        }
        Optional<TeacherIncome> teacherIncomeOtp = teacherIncomeRepository
                .findByCourseIdAndUserIdAndMonthAndYear(teacherIncomeRequest.getCourse().getId(),
                        teacherIncomeRequest.getUserId(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        if (!teacherIncomeOtp.isEmpty()) {
            TeacherIncome teacherIncome = teacherIncomeOtp.get();
            teacherIncome.setMoney(teacherIncome.getMoney() + teacherIncomeRequest.getAmount());
            teacherIncomeRepository.save(teacherIncome);
        } else {
            TeacherIncome teacherIncome = teacherIncomeMapper.mapRequestDtoToEntity(teacherIncomeRequest);
            teacherIncome.setMonth(LocalDate.now().getMonthValue());
            teacherIncome.setYear(LocalDate.now().getYear());
            teacherIncome.setStatus(TeacherIncomeStatus.PENDING);
            teacherIncomeRepository.save(teacherIncome);
        }
    }

    @Override
    public List<TeacherIncomeResponse> getCurrentTeacherIncome() {
        Long userId = securityContextService.getCurrentUser().getId();
        List<CourseReportInterface> courseReportInterfaces = teacherIncomeRepository
                .getRevenueByTeacherEmailForLast10Months(userId);
        if (courseReportInterfaces == null || courseReportInterfaces.isEmpty()) {
            return Collections.emptyList();
        }
        return teacherIncomeMapper.mapEntitiesToResponseDtos(courseReportInterfaces);
    }

    @Override
    public List<TeacherIncomeResponse> getCurrentTeacherIncomeByCourseId(Long courseId) {
        Long userId = securityContextService.getCurrentUser().getId();
        List<CourseReportInterface> courseReportInterfaces = teacherIncomeRepository
                .getRevenueByTeacherEmailAndCourseIdForLast10Months(userId, courseId);
        if (courseReportInterfaces == null || courseReportInterfaces.isEmpty()) {
            return Collections.emptyList();
        }
        return teacherIncomeMapper.mapEntitiesToResponseDtos(courseReportInterfaces);
    }

    @Override
    public List<CourseRevenueByMonth> getCurrentTeacherIncomeIn10Motnh() {
        Long userId = securityContextService.getCurrentUser().getId();
        List<CourseRevenueByMonthInterface> courseRevenueByMonths = teacherIncomeRepository
                .getTeacherRevenueByMonth(userId);
        return teacherIncomeMapper.mapToCourseRevenueByMonths(courseRevenueByMonths);
    }

}
