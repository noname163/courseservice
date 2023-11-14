package com.example.courseservice.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.VideoTemporary;

public interface VideoTemporaryRepository extends JpaRepository<VideoTemporary, Long> {
    public Optional<VideoTemporary> findByVideoId(Long videoId);
}
