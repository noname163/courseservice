package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.request.TeacherIncomeRequest;
import com.example.courseservice.data.dto.response.CourseRevenueByMonth;
import com.example.courseservice.data.dto.response.TeacherIncomeForAdmin;
import com.example.courseservice.data.dto.response.TeacherIncomeResponse;
import com.example.courseservice.data.entities.TeacherIncome;
import com.example.courseservice.data.entities.Transaction;
import com.example.courseservice.data.object.CourseReportInterface;
import com.example.courseservice.data.object.CourseRevenueByMonthInterface;

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

    public CourseRevenueByMonth mapToCourseRevenueByMonth(CourseRevenueByMonthInterface courseRevenueByMonth) {
        return CourseRevenueByMonth
                .builder()
                .id(courseRevenueByMonth.getId())
                .revenue(courseRevenueByMonth.getRevenue())
                .month(courseRevenueByMonth.getMonth())
                .build();
    }

    public List<CourseRevenueByMonth> mapToCourseRevenueByMonths(
            List<CourseRevenueByMonthInterface> courseRevenueByMonthInterfaces) {
        return courseRevenueByMonthInterfaces.stream().map(this::mapToCourseRevenueByMonth)
                .collect(Collectors.toList());
    }

    public TeacherIncomeForAdmin mapToTeacherIncomeForAdmin(CourseReportInterface teacherIncomeForAdminInterface) {
        if (teacherIncomeForAdminInterface == null) {
            return null;
        }

        return TeacherIncomeForAdmin
                .builder()
                .id(teacherIncomeForAdminInterface.getId())
                .revenue(teacherIncomeForAdminInterface.getRevenue())
                .courseId(teacherIncomeForAdminInterface.getCourseId())
                .paymentDate(teacherIncomeForAdminInterface.getPaymentDate())
                .receivedMoney(teacherIncomeForAdminInterface.getReceivedMoney())
                .courseName(teacherIncomeForAdminInterface.getCourseName())
                .monthOfYear(teacherIncomeForAdminInterface.getMonthOfYear())
                .teacherIncomeStatus(teacherIncomeForAdminInterface.getTeacherIncomeStatus())
                .build();
    }

    public List<TeacherIncomeForAdmin> mapToTeacherIncomeForAdminList(
            List<CourseReportInterface> teacherIncomeForAdminInterfaces) {
        return teacherIncomeForAdminInterfaces.stream()
                .map(this::mapToTeacherIncomeForAdmin)
                .collect(Collectors.toList());
    }
}
