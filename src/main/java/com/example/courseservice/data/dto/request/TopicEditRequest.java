package com.example.courseservice.data.dto.request;

import java.util.List;

import com.example.courseservice.data.object.Topic;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TopicEditRequest {
    private List<Topic> topics;
    private Long courseId;
}
