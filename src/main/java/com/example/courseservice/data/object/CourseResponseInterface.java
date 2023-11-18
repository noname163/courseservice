package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.CommonStatus;

public interface CourseResponseInterface {

    long getId();

    String getThumbnial();

    String getTeacherName();

    String getCourseName();

    Float getAverageRating();

    int getNumberOfRate();

    int getTotalVideo();

    String getSubject();

    String getLevel();

    double getPrice();

    LocalDateTime getCreatedDate();

    LocalDateTime getUpdateDate();

    CommonStatus getStatus();

}