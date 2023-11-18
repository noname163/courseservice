package com.example.courseservice.data.object;

import com.example.courseservice.data.constants.VideoStatus;

public interface VideoItemResponseInterface {
    Long getId();
    String getName();
    Float getDuration();
    VideoStatus getVideoStatus();
    Boolean getIsAccess();
}
