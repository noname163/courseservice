package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.CourseTemporary;

public interface CourseTemporaryRepository extends JpaRepository<CourseTemporary, Long> {
    
}
