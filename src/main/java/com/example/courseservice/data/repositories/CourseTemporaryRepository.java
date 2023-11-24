package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.object.CourseResponseInterface;

public interface CourseTemporaryRepository extends JpaRepository<CourseTemporary, Long> {
    public Optional<CourseTemporary> findByCourseId(Long courseId);

    public Optional<CourseTemporary> findByIdAndStatusNot(Long id, CommonStatus commonStatus);

    public Optional<CourseTemporary> findByIdAndStatus(Long id, CommonStatus commonStatus);

    @Query("SELECT ct.id AS id, " +
            "ct.thumbnial AS thumbnial, " +
            "ct.teacherName AS teacherName, " +
            "ct.name AS courseName, " +
            "SIZE(ct.videoTemporaries) AS totalVideo, " +
            "ct.subject AS subject, " +
            "l.name AS level, " +
            "ct.price AS price, " +
            "ct.createDate AS createdDate, " +
            "ct.updateTime AS updateDate, " +
            "ct.status AS status " +
            "FROM CourseTemporary ct " +
            "LEFT JOIN ct.videoTemporaries vt " +
            "LEFT JOIN ct.courseTopics ctt " +
            "LEFT JOIN Level l ON ct.levelId = l.id " +
            "WHERE ct.status <> :status " +
            "GROUP BY ct.id, l.name")
    Page<CourseResponseInterface> getByStatusNot(@Param("status") CommonStatus status, Pageable pageable);

    @Query("SELECT ct.id AS id, " +
            "ct.thumbnial AS thumbnial, " +
            "ct.teacherName AS teacherName, " +
            "ct.name AS courseName, " +
            "SIZE(ct.videoTemporaries) AS totalVideo, " +
            "ct.subject AS subject, " +
            "l.name AS level, " +
            "ct.price AS price, " +
            "ct.createDate AS createdDate, " +
            "ct.updateTime AS updateDate, " +
            "ct.status AS status " +
            "FROM CourseTemporary ct " +
            "LEFT JOIN ct.videoTemporaries vt " +
            "LEFT JOIN ct.courseTopics ctt " +
            "LEFT JOIN Level l ON ct.levelId = l.id " +
            "WHERE ct.status <> :status " +
            "AND ct.teacherEmail = :email " +
            "GROUP BY ct.id, l.name")
    Page<CourseResponseInterface> getByEmailAndStatusNot(@Param("email") String email,
            @Param("status") CommonStatus status, Pageable pageable);

    List<CourseTemporary> findByTeacherIdAndIdIn(Long teacherId, List<Long> courseIds);

    Optional<CourseTemporary> findByTeacherIdAndId(Long teacherId, Long courseId);

    Boolean existsByTeacherIdAndId(Long teacherId, Long courseId);
}
