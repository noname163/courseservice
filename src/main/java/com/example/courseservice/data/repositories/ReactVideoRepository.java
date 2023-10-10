package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.ReactVideo;

public interface ReactVideoRepository extends JpaRepository<ReactVideo, Long> {
    
}
