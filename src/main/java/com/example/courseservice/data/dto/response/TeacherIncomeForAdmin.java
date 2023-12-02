package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.TeacherIncomeStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class TeacherIncomeForAdmin {
    private Long id;
    private String teacherAvatar;
    private String teacherName;
    private String subject;
    private String courseName;
    private Double revenue;
    private Double receivedMoney;
    private LocalDateTime paymentDate;
    private Long courseId;
    private String monthOfYear;
    private TeacherIncomeStatus teacherIncomeStatus;
}
