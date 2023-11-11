package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseTopicRequest {
    private String topicName;
    private Long courseId;
}
