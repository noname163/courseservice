package com.example.courseservice.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.CourseTemporary;

public interface CourseTemporaryRepository extends JpaRepository<CourseTemporary, Long> {
    public Optional<CourseTemporary> findByCourseId(Long courseId);
    public Optional<CourseTemporary> findByIdAndStatusNot(Long id, CommonStatus commonStatus);
    public Optional<CourseTemporary> findByIdAndStatus(Long id, CommonStatus commonStatus);
}
