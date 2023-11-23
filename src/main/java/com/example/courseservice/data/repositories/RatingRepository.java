package com.example.courseservice.data.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    public Optional<Rating> findByStudentIdAndCourseId(Long studentId, Long courseId);
    public Page<Rating> findByCourseId(Long courseId, Pageable pageable);
}
