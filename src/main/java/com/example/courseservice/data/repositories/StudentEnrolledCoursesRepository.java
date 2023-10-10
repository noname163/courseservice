package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.StudentEnrolledCourses;

public interface StudentEnrolledCoursesRepository extends JpaRepository<StudentEnrolledCourses, Long> {
    
}
