package com.example.courseservice.services.coursetopicservice;

import java.util.List;

import com.example.courseservice.data.dto.request.CourseTopicRequest;
import com.example.courseservice.data.entities.CourseTopic;
import com.example.courseservice.data.object.Topic;

public interface CourseTopicService {
    public void createCourseTopic(CourseTopicRequest courseTopicRequest);
    public void createCourseTopics(List<CourseTopicRequest> courseTopicRequests);
    public List<CourseTopic> courseTopicsByString(List<Topic> courseTopicsName);
}
