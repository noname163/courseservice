package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.VideoStatus;

public interface VideoAdminResponseInterface {
    Long getId();
    String getUrl();
    String getName();
    String getTeacherName();
    String getTeacherAvatar();
    String getDescription();
    String getSubject();
    String getThumbnail();
    String getMaterial();
    String getCourseName();
    Long getLike();
    Long getDislike();
    Float getDuration();
    CommonStatus getStatus();
    LocalDateTime getCreatedDate();
    LocalDateTime getUpdateDate();
    VideoStatus getVideoStatus();
    Integer getOrdinalNumber();
}
