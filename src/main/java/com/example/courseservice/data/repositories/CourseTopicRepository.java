package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTopic;

public interface CourseTopicRepository extends JpaRepository<CourseTopic, Long> {

    @Query("SELECT ct.topicName FROM CourseTopic ct WHERE ct.course.id = :courseId")
    List<String> getTopicNamesByCourseId(@Param("courseId") Long courseId);

    List<CourseTopic> findByCourseIdAndTopicNameIn(Long courseId, Set<String> name);

    List<CourseTopic> findByCourse(Course course);

}
