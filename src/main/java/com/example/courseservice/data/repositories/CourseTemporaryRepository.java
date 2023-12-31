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
            "ct.teacherEmail AS teacherEmail, " +
            "ct.teacherAvatar AS teacherAvatar, " +
            "ct.course.id AS courseRealId, " +
            "ct.teacherId AS teacherId, " +
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
            "ct.teacherEmail AS teacherEmail, " +
            "ct.teacherAvatar AS teacherAvatar, " +
            "ct.course.id AS courseRealId, " +
            "ct.teacherId AS teacherId, " +
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
            "WHERE ct.status = :status " +
            "GROUP BY ct.id, l.name")
    Page<CourseResponseInterface> filterByStatus(@Param("status") CommonStatus status, Pageable pageable);

    @Query("SELECT ct.id AS id, " +
            "ct.thumbnial AS thumbnial, " +
            "ct.teacherName AS teacherName, " +
            "ct.teacherAvatar AS teacherAvatar, " +
            "ct.name AS courseName, " +
            "ct.course.id AS courseRealId, " +
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

    @Query("SELECT ct.id AS id, " +
            "ct.thumbnial AS thumbnial, " +
            "ct.teacherName AS teacherName, " +
            "ct.teacherAvatar AS teacherAvatar, " +
            "ct.name AS courseName, " +
            "ct.course.id AS courseRealId, " +
            "SIZE(ct.videoTemporaries) AS totalVideo, " +
            "ct.subject AS subject, " +
            "l.name AS level, " +
            "0 AS totalCompletedVideo, " +
            "true AS isAccess, " +
            "0.0 AS progress, " +
            "ct.price AS price, " +
            "ct.createdDate AS createdDate, " +
            "ct.updateTime AS updateDate, " +
            "ct.status AS status " +
            "FROM CourseTemporary ct " +
            "LEFT JOIN ct.videoTemporaries vt " +
            "LEFT JOIN ct.courseTopics ctt " +
            "LEFT JOIN Level l ON ct.levelId = l.id " +
            "WHERE (:name is null or lower(ct.name) like lower(concat('%', :name, '%'))) " +
            "AND (:status = 'ALL' or ct.status = :status) " +
            "AND ct.teacherEmail = :email " +
            "GROUP BY ct.id, l.name")
    Page<CourseResponseInterface> searchByNameForTeacher(@Param("name") String name,
            @Param("email") String email,
            @Param("status") String status, Pageable pageable);

    @Query("SELECT ct.id AS id, " +
            "ct.thumbnial AS thumbnial, " +
            "ct.teacherName AS teacherName, " +
            "ct.teacherAvatar AS teacherAvatar, " +
            "ct.name AS courseName, " +
            "ct.course.id AS courseRealId, " +
            "SIZE(ct.videoTemporaries) AS totalVideo, " +
            "ct.subject AS subject, " +
            "l.name AS level, " +
            "0 AS totalCompletedVideo, " +
            "true AS isAccess, " +
            "0.0 AS progress, " +
            "ct.price AS price, " +
            "ct.createdDate AS createdDate, " +
            "ct.updateTime AS updateDate, " +
            "ct.status AS status " +
            "FROM CourseTemporary ct " +
            "LEFT JOIN ct.videoTemporaries vt " +
            "LEFT JOIN ct.courseTopics ctt " +
            "LEFT JOIN Level l ON ct.levelId = l.id " +
            "WHERE (:name is null or lower(ct.name) like lower(concat('%', :name, '%'))) " +
            "AND ct.status = :status " +
            "GROUP BY ct.id, l.name")
    Page<CourseResponseInterface> searchByNameForAdmin(@Param("name") String name,
            @Param("status") CommonStatus status, Pageable pageable);

    @Query("SELECT ct.id AS id, " +
            "ct.thumbnial AS thumbnial, " +
            "ct.teacherName AS teacherName, " +
            "ct.teacherAvatar AS teacherAvatar, " +
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
            "WHERE ct.status = :status " +
            "AND ct.teacherEmail = :email " +
            "GROUP BY ct.id, l.name")
    Page<CourseResponseInterface> filterByEmailAndStatus(@Param("email") String email,
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
