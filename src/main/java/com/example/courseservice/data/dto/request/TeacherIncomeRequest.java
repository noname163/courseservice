package com.example.courseservice.data.dto.request;

import com.example.courseservice.data.entities.Course;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TeacherIncomeRequest {
    private Long userId;
    private Course course;
    private Long amount;
}
