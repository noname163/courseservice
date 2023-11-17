package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TeacherIncomeResponse {
    private String courseName;
    private Double revenue;
    private Long courseId;
    private String monthOfYear;
}
