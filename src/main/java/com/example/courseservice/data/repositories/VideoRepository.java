package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByCourseAndStatus(Course course, CommonStatus status, Pageable pageable);
    List<Video> findByCourseAndStatus(Course course, CommonStatus status);
    Optional<Video> findByIdAndStatus(Long id, CommonStatus status);
}
