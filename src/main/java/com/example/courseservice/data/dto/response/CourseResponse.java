package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponse {
    private String thumbinial;
    private String teacherName;
    private String courseName;
    private float rating;
    private int numberOfRate;
    private int totalVideo;
    private String subject;
    private String level;
    private double price;
}
