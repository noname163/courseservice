package com.example.courseservice.services.coursetopicservice.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.dto.request.CourseTopicRequest;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTopic;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.CourseTopicRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;

@Service
public class CourseTopicServiceImpl implements CourseTopicService {

    @Autowired
    private CourseTopicRepository courseTopicRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void createCourseTopic(CourseTopicRequest courseTopicRequest) {
        Course course = courseRepository.findById(courseTopicRequest.getCourseId())
                .orElseThrow(
                        () -> new BadRequestException(
                                "Can not find course witd id" + courseTopicRequest.getCourseId()));

        courseTopicRepository.save(CourseTopic
                .builder()
                .course(course)
                .topicName(courseTopicRequest.getTopicName())
                .build());
    }

    @Override
    public void createCourseTopics(List<CourseTopicRequest> courseTopicRequests) {
        Set<Long> courseIds = courseTopicRequests
                .stream()
                .map(CourseTopicRequest::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toSet());
        List<CourseTopic> courseTopics = new ArrayList<>();
        List<Course> courses = courseRepository.findByIdInAndCommonStatus(courseIds, CommonStatus.AVAILABLE);
        for (CourseTopicRequest courseTopicRequest : courseTopicRequests) {
            Optional<Course> matchingCourse = courses.stream()
                    .filter(course -> course.getId().equals(courseTopicRequest.getCourseId()))
                    .findFirst();
            if (!matchingCourse.isPresent()) {
                throw new BadRequestException("Can not found course with id " + courseTopicRequest.getCourseId());
            }
            CourseTopic courseTopic = CourseTopic.builder()
                    .topicName(courseTopicRequest.getTopicName())
                    .course(matchingCourse.get())
                    .build();
            courseTopics.add(courseTopic);

        }
        courseTopicRepository.saveAll(courseTopics);
    }

    @Override
    public List<CourseTopic> courseTopicsByString(List<String> courseTopicsName) {
        return courseTopicsName
                .stream()
                .distinct()
                .map(name -> CourseTopic.builder().topicName(name).build())
                .collect(Collectors.toList());
    }

}
