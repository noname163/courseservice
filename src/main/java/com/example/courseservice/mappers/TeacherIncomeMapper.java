package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.request.TeacherIncomeRequest;
import com.example.courseservice.data.dto.response.TeacherIncomeResponse;
import com.example.courseservice.data.entities.TeacherIncome;
import com.example.courseservice.data.entities.Transaction;
import com.example.courseservice.data.object.CourseReportInterface;

@Component
public class TeacherIncomeMapper {
    public TeacherIncome mapRequestDtoToEntity(TeacherIncomeRequest teacherIncomeRequest) {
        return TeacherIncome
                .builder()
                .course(teacherIncomeRequest.getCourse())
                .money(teacherIncomeRequest.getAmount())
                .userId(teacherIncomeRequest.getUserId())
                .build();
    }

    public TeacherIncomeRequest mapTransactionToTeacherIncomeRequest(Transaction transaction) {
        return TeacherIncomeRequest
                .builder()
                .amount(transaction.getAmount())
                .course(transaction.getCourse())
                .userId(transaction.getCourse().getTeacherId())
                .build();
    }

    public TeacherIncomeResponse mapEntityToResponseDto(CourseReportInterface courseReportInterface) {
        return TeacherIncomeResponse
                .builder()
                .courseId(courseReportInterface.getCourseId())
                .courseName(courseReportInterface.getCourseName())
                .monthOfYear(courseReportInterface.getMonthOfYear())
                .revenue(courseReportInterface.getRevenue())
                .build();
    }

    public List<TeacherIncomeResponse> mapEntitiesToResponseDtos(List<CourseReportInterface> courseReportInterfaces) {
        return courseReportInterfaces
                .stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }
}
