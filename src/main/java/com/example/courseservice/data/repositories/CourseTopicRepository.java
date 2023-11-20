package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.entities.CourseTopic;

public interface CourseTopicRepository extends JpaRepository<CourseTopic, Long> {
    @Modifying
    @Query("UPDATE CourseTopic ct SET ct.course.id = :courseId WHERE ct.courseTemporary.id = :courseTemporaryId")
    void updateCourseIdByCourseTemporaryId(
            @Param("courseId") Long courseId,
            @Param("courseTemporaryId") Long courseTemporaryId);

}
