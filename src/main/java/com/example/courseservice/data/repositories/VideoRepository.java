package com.example.courseservice.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByCourse(Course course);
}
