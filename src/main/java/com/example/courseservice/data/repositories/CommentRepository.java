package com.example.courseservice.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByVideoIdAndCommonStatus(Long videoId, CommonStatus commonStatus, Pageable pageable);
}
