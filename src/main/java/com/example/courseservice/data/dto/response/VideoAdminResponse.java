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
    private String name;
    private String courseName;
    private long like;
    private long dislike;
    private CommonStatus status;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private VideoStatus videoStatus;
}