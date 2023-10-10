package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    
}
