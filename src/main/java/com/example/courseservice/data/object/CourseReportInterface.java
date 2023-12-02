package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.TeacherIncomeStatus;

public interface CourseReportInterface {
    Long getId();
    Long getCourseId();
    String getTeacherName();
    String getTeacherAvatar();
    String getSubject();
    String getCourseName();
    String getMonthOfYear();
    Double getRevenue();
    Double getReceivedMoney();
    LocalDateTime getPaymentDate();
    TeacherIncomeStatus getTeacherIncomeStatus();
}
