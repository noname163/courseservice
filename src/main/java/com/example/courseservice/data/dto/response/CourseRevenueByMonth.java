package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseRevenueByMonth {
    private Long id;
    private Double revenue;
    private String month;
}
