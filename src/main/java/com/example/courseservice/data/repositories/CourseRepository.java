package com.example.courseservice.data.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    public Page<Course> findCourseByTeacherEmailAndCommonStatus(String email, CommonStatus commonStatus, Pageable pageable);
    public Page<Course> findCourseByTeacherEmail(String email, Pageable pageable);
    public Page<Course> findByCommonStatus(Pageable pageable, CommonStatus commonStatus);
}
