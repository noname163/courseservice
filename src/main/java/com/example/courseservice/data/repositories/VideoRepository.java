package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    
}
