package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.dto.response.CourseResponse;

public interface CourseDetailResponseInterface {
    Long getId();
    int getTotalStudent();
    String getDescription();
    String getName();
    String getthumbnail();
    String getTeacherName();
    Float getAverageRating();
    Integer getNumberOfRate();
    Integer getTotalVideo();
    String getSubject();
    String getLevel();
    Double getPrice();
    LocalDateTime getCreatedDate();
    LocalDateTime getUpdateDate();
    CommonStatus getStatus();
}
