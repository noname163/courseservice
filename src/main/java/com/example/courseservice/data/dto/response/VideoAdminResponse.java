package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.VideoStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VideoAdminResponse {
    private String subject;
    private String courseName;
    private Long dislike;
    private CommonStatus status;
    private VideoStatus videoStatus;
    private String teacherName;
    private String teacherAvatar;
}
