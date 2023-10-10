package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
}
