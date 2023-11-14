package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.VideoTemporary;

public interface VideoTemporaryRepository extends JpaRepository<VideoTemporary, Long> {
    
}
