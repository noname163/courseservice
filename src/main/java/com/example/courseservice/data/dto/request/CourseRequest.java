package com.example.courseservice.data.dto.request;

import java.util.List;

import com.example.courseservice.data.object.Subject;
import com.example.courseservice.data.object.Topic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRequest {
    private String description;
    private String name;
    private Double price;
    private Subject subject;
    private long levelId;
    private List<Topic> topic;
}
