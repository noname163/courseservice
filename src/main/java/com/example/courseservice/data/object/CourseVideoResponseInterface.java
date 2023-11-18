package com.example.courseservice.data.object;

public interface CourseVideoResponseInterface {
    Long getId();
    String getName();
    String getThumbnail();
    float getDuration();
    Long getTotalLike();
    Integer getTotalComment();
    Integer getOrdinalNumber();
}
