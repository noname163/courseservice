package com.example.courseservice.data.dto.request;

import com.example.courseservice.data.object.Topic;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseTopicRequest {
    private Topic topic;
    private Long courseId;
}
