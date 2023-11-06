package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Level;

public interface LevelRepository extends JpaRepository<Level, Long> {
    
}