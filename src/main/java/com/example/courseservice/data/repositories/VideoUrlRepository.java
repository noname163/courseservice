package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.VideoUrl;

public interface VideoUrlRepository extends JpaRepository<VideoUrl,Long> {
    
}
