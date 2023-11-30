package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VideoAdminResponse {
    private Long id;
    private String url;
    private String name;
    private String teacherName;
    private String description;
    private String subject;
    private String thumbnail;
    private String material;
    private String courseName;
    private long like;
    private long dislike;
    private float duration;
    private CommonStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private VideoStatus videoStatus;
    private Integer ordinalNumber;
}
