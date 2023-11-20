package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.VideoTemporary;

public interface VideoTemporaryRepository extends JpaRepository<VideoTemporary, Long> {
    public Optional<VideoTemporary> findByVideoId(Long videoId);

    public List<VideoTemporary> findByCourseTemporaryIdAndStatus(Long courseTemporaryId, CommonStatus commonStatus);

    public Boolean existsByVideoId(Long videoId);

    @Query("SELECT MAX(v.ordinalNumber) FROM VideoTemporary v WHERE v.courseTemporary = :course")
    public Integer findMaxOrdinalNumberByCourse(@Param("course") CourseTemporary course);
}
