package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.ReplyComment;

public interface ReplyCommentRepository extends JpaRepository<ReplyComment, Long> {
    
}
