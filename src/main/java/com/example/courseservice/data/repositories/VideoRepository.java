package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByCourseAndStatus(Course course, CommonStatus status, Pageable pageable);
    Page<Video> findByStatus(CommonStatus status, Pageable pageable);
    Page<Video> findByStatusAndCourseIn(CommonStatus status, List<Course> courses,Pageable pageable);
    Page<Video> findByCourseIn(List<Course> courses,Pageable pageable);
    List<Video> findByCourseAndStatus(Course course, CommonStatus status);
    Optional<Video> findByIdAndStatus(Long id, CommonStatus status);
    Optional<Video> findByIdAndStatusNot(Long id, CommonStatus status);
}
