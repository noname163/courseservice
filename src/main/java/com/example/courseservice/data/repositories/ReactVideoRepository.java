package com.example.courseservice.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.ReactVideo;

public interface ReactVideoRepository extends JpaRepository<ReactVideo, Long> {
    Optional<ReactVideo> findByVideoIdAndStudentId(Long videoId, Long studentId);
}
