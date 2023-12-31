package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.CommonStatus;

public interface CourseResponseInterface {

    Long getId();

    Long getCourseRealId();

    Long getTeacherId();

    String getThumbnial();

    String getTeacherName();

    String getTeacherAvatar();

    String getCourseName();

    Float getAverageRating();

    Integer getNumberOfRate();

    Integer getTotalVideo();

    Integer getTotalCompletedVideo();

    String getSubject();

    String getLevel();

    double getPrice();

    LocalDateTime getCreatedDate();

    LocalDateTime getUpdateDate();

    CommonStatus getStatus();

    Boolean getIsAccess();

    Float getProgress();
}
