package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByCourse(Course course);
    Optional<Video> findByIdAndStatus(Long id, CommonStatus status);
}
