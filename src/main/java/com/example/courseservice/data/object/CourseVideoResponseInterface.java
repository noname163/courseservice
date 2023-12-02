package com.example.courseservice.data.object;

import com.example.courseservice.data.constants.VideoStatus;

public interface CourseVideoResponseInterface {
    Long getId();
    String getName();
    String getUrl();
    String getThumbnail();
    float getDuration();
    Long getTotalLike();
    Integer getTotalComment();
    Integer getOrdinalNumber();
    VideoStatus getVideoStatus();
    Boolean getIsDraft();
    Boolean getIsWatched();
}
