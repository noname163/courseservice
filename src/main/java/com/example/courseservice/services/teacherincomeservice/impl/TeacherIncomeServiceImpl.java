package com.example.courseservice.services.teacherincomeservice.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.dto.request.TeacherIncomeRequest;
import com.example.courseservice.data.dto.response.TeacherIncomeResponse;
import com.example.courseservice.data.entities.TeacherIncome;
import com.example.courseservice.data.object.CourseReportInterface;
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
        if (teacherIncomeOtp.isPresent()) {
            TeacherIncome teacherIncome = teacherIncomeOtp.get();
            teacherIncome.setMoney(teacherIncome.getMoney() + teacherIncomeRequest.getAmount());
            teacherIncomeRepository.save(teacherIncome);
        }
        teacherIncomeRepository.save(teacherIncomeMapper.mapRequestDtoToEntity(teacherIncomeRequest));
    }

    @Override
    public List<TeacherIncomeResponse> getCurrentTeacherIncome() {
        String email = securityContextService.getCurrentUser().getEmail();
        List<CourseReportInterface> courseReportInterfaces = teacherIncomeRepository
                .getRevenueByTeacherEmailForLast10Months(email);
        if (courseReportInterfaces == null || courseReportInterfaces.isEmpty()) {
            return Collections.emptyList();
        }
        return teacherIncomeMapper.mapEntitiesToResponseDtos(courseReportInterfaces);
    }

    @Override
    public List<TeacherIncomeResponse> getCurrentTeacherIncomeByCourseId(Long courseId) {
        String email = securityContextService.getCurrentUser().getEmail();
        List<CourseReportInterface> courseReportInterfaces = teacherIncomeRepository
                .getRevenueByTeacherEmailAndCourseIdForLast10Months(email, courseId);
        if (courseReportInterfaces == null || courseReportInterfaces.isEmpty()) {
            return Collections.emptyList();
        }
        return teacherIncomeMapper.mapEntitiesToResponseDtos(courseReportInterfaces);
    }

}
