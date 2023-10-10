package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.CourseTopic;

public interface CourseTopicRepository extends JpaRepository<CourseTopic, Long> {

}
