package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseRevenueByMonth {
    private Double revenue;
    private Integer month;
    private Integer year;
}
