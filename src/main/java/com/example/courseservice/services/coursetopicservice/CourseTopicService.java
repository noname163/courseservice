package com.example.courseservice.services.coursetopicservice;

import java.util.List;

import com.example.courseservice.data.dto.request.CourseTopicRequest;
import com.example.courseservice.data.dto.request.TopicEditRequest;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.CourseTopic;
import com.example.courseservice.data.object.Topic;

public interface CourseTopicService {
    public void createCourseTopic(CourseTopicRequest courseTopicRequest);
    public void createCourseTopics(List<CourseTopicRequest> courseTopicRequests);
    public void createCourseTopics(List<Topic>  courseTopicRequests, CourseTemporary courseTemporary);
    public void updateCourseTopicByCourseTemporary(CourseTemporary courseTemporary, Course course);
    public void addTopicByCourseId(TopicEditRequest topicEditRequest);
    public void addTopicByCourseTmpId(TopicEditRequest topicEditRequest);
    public void removeTopicByCourseId(TopicEditRequest topicEditRequest);
    public void removeTopicByCourseTmpId(TopicEditRequest topicEditRequest);
    public void removeTopicByCourseTmp(CourseTemporary courseTemporaryId);
    public List<CourseTopic> courseTopicsByString(List<Topic> courseTopicsName);
    public List<String> getTopicsByCourseId(Long courseId);
    public void addCourseTemporaryToTopic(Course course, CourseTemporary courseTemporary);
    public List<String> getTopicsByCourseTmpId(Long courseTmpId);
}
