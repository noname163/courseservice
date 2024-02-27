package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.courseservice.data.constants.CommonStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseDetailResponse{
    private Long id;
    private Long courseRealId;
    private int totalStudent;
    private String description;
    private String name;
    private String thumbnail;
    private String teacherName;
    private String teacherAvatar;
    private String teacherEmail;
    private Long teacherId;
    private float rating;
    private int numberOfRate;
    private int totalVideo;
    private String subject;
    private String level;
    private double price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private CommonStatus status;
    private Integer totalCompleted;
    private Float progress;
    private Boolean isAccess;
    private List<String> topics;
}
