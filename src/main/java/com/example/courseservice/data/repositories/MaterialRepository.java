package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    
}
