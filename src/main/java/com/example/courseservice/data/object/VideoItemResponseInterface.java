package com.example.courseservice.data.object;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.VideoStatus;

public interface VideoItemResponseInterface {
    Long getId();
    String getName();
    Float getDuration();
    VideoStatus getVideoStatus();
    LocalDateTime getCreateDate();
    Boolean getIsAccess();
}
