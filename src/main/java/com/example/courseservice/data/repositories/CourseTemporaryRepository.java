package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.object.CourseResponseInterface;

public interface CourseTemporaryRepository extends JpaRepository<CourseTemporary, Long> {
    public Optional<CourseTemporary> findByCourseId(Long courseId);

    public CourseTemporary findByCourse(Course course);

    public Optional<CourseTemporary> findByIdAndStatusNot(Long id, CommonStatus commonStatus);

    public Optional<CourseTemporary> findByIdAndStatus(Long id, CommonStatus commonStatus);

    @Query("SELECT ct.id AS id, " +
            "ct.thumbnial AS thumbnial, " +
            "ct.teacherName AS teacherName, " +
            "ct.teacherEmail AS teacherEmail, "+
            "ct.teacherAvatar AS teacherAvatar, "+
            "ct.teacherId AS teacherId, "+
            "ct.name AS courseName, " +
            "SIZE(ct.videoTemporaries) AS totalVideo, " +
            "ct.subject AS subject, " +
            "l.name AS level, " +
            "ct.price AS price, " +
            "ct.createdDate AS createdDate, " +
            "ct.updateTime AS updateDate, " +
            "ct.status AS status " +
            "FROM CourseTemporary ct " +
            "LEFT JOIN ct.videoTemporaries vt " +
            "LEFT JOIN ct.courseTopics ctt " +
            "LEFT JOIN Level l ON ct.levelId = l.id " +
            "WHERE ct.status NOT IN :status " +
            "GROUP BY ct.id, l.name")
    Page<CourseResponseInterface> getByStatusNot(@Param("status") List<CommonStatus> status, Pageable pageable);

    @Query("SELECT ct.id AS id, " +
            "ct.thumbnial AS thumbnial, " +
            "ct.teacherName AS teacherName, " +
            "ct.teacherAvatar AS teacherAvatar, "+
            "ct.name AS courseName, " +
            "SIZE(ct.videoTemporaries) AS totalVideo, " +
            "ct.subject AS subject, " +
            "l.name AS level, " +
            "ct.price AS price, " +
            "ct.createdDate AS createdDate, " +
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

    @Query("SELECT ct FROM CourseTemporary ct " +
            "WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(ct.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR EXISTS (SELECT 1 FROM CourseTopic ctt WHERE ctt.courseTemporary = ct AND LOWER(ctt.topicName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ")
    Page<CourseTemporary> searchCourseTemporaries(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT ct FROM CourseTemporary ct " +
            "WHERE (LOWER(ct.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(ct.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR EXISTS (SELECT 1 FROM CourseTopic ctt WHERE ctt.courseTemporary = ct AND LOWER(ctt.topicName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))) "
            +
            "AND ct.teacherId = :teacherId")
    Page<CourseTemporary> searchCourseTemporariesByTeacher(@Param("searchTerm") String searchTerm,
            @Param("teacherId") Long teacherId,
            Pageable pageable);

    List<CourseTemporary> findByTeacherIdAndIdIn(Long teacherId, List<Long> courseIds);

    Optional<CourseTemporary> findByTeacherIdAndId(Long teacherId, Long courseId);

    Boolean existsByTeacherIdAndId(Long teacherId, Long courseId);
}
