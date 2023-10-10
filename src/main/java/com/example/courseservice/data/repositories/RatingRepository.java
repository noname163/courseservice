package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    
}
