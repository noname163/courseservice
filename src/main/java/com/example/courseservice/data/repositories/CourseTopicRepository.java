package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.CourseTopic;

public interface CourseTopicRepository extends JpaRepository<CourseTopic, Long> {

    @Query("UPDATE CourseTopic ct SET ct.course.id = :courseId WHERE ct.courseTemporary.id = :courseTemporaryId")
    public void updateCourseIdByCourseTemporaryId(
            @Param("courseId") Long courseId,
            @Param("courseTemporaryId") Long courseTemporaryId);

    public Optional<CourseTopic> findByCourseTemporary(CourseTemporary courseTemporary);

    @Query("SELECT ct.topicName FROM CourseTopic ct WHERE ct.course.id = :courseId")
    List<String> getTopicNamesByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT ct.topicName FROM CourseTopic ct WHERE ct.courseTemporary.id = :courseTmpId")
    List<String> getTopicNamesByCourseTmpId(@Param("courseTmpId") Long courseTmpId);

}
