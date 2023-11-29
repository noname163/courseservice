package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.CommonStatus;

public interface CourseResponseInterface {

    long getId();

    Long getTeacherId();

    String getThumbnial();

    String getTeacherName();

    String getTeacherAvatar();

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

    Boolean getIsAccess();

    Float getProgress();
}
