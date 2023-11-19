package com.example.courseservice.data.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.Comment;
import com.example.courseservice.data.entities.Video;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByVideoIdAndCommonStatus(Long videoId, CommonStatus commonStatus, Pageable pageable);
    List<Comment> findByVideo(Video video);
    Void deleteByVideo(Video video);
}
