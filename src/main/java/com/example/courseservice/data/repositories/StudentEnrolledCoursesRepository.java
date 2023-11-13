package com.example.courseservice.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.StudentEnrolledCourses;

public interface StudentEnrolledCoursesRepository extends JpaRepository<StudentEnrolledCourses, Long> {
    Page<StudentEnrolledCourses> findByStudentEmail(String email, Pageable pageable);
}
