package com.example.courseservice.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Level;

public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findByIdIn(List<Long> ids);
}
