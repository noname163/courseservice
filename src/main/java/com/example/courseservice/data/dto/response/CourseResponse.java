package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.CommonStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponse {
    private long id;
    private String thumbnail;
    private String teacherName;
    private String teacherAvatar;
    private String courseName;
    private float rating;
    private int numberOfRate;
    private int totalVideo;
    private int totalCompletedVideo;
    private String subject;
    private String level;
    private double price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private CommonStatus status;
    private Boolean isAccess;
    private Long teacherId;
    private Float progress;
    private Long courseRealId;
}
