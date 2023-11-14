package com.example.courseservice.data.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.StudentEnrolledCourses;

public interface StudentEnrolledCoursesRepository extends JpaRepository<StudentEnrolledCourses, Long> {
    Page<StudentEnrolledCourses> findByStudentEmail(String email, Pageable pageable);
    Optional<StudentEnrolledCourses> findByStudentEmailAndCourseId(String email, Long courseId);
}
