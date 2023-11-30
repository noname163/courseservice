package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.entities.StudentVideoProgress;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.CourseProgressInterface;
import com.example.courseservice.data.object.CourseResponseInterface;

public interface StudentVideoProgressRepository extends JpaRepository<StudentVideoProgress, Long> {
    @Query("SELECT c.id AS id, " +
            "c.teacherId AS teacherId, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "TRUE AS isAccess, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status, " +
            "COUNT(sp) AS totalCompletedVideo, " +
            "(COUNT(sp) / CAST(SIZE(c.videos) AS float)) * 100 AS progress " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "LEFT JOIN c.studentVideoProgresses sp WITH sp.course.id = c.id AND sp.studentId = :studentId AND sp.isCompleted = TRUE "
            +
            "WHERE c.id IN (SELECT sec.course.id FROM StudentEnrolledCourses sec WHERE sec.studentId = :studentId) "
            +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getCourseDetailsByStudentId(
            @Param("studentId") Long studentId,
            Pageable pageable);

    Boolean existsByStudentIdAndVideo(Long studentId, Video video);

    @Query("SELECT sp.video.id FROM StudentVideoProgress sp " +
            "WHERE sp.studentId = :studentId AND sp.course.id = :courseId AND sp.isCompleted = TRUE")
    Set<Long> getCompletedVideoIdsByStudentAndCourse(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId);

    @Query("SELECT " +
            "COALESCE((COUNT(svp) / CAST(SIZE(v) AS float)) * 100, 0) AS progress, "
            +
            "svp.course.id AS courseId " +
            "FROM StudentVideoProgress svp " +
            "JOIN svp.course c " +
            "JOIN c.videos v " +
            "WHERE svp.studentId = :studentId " +
            "GROUP BY svp.course.id, c.id")
    List<CourseProgressInterface> getCourseProgressByStudentId(@Param("studentId") Long studentId);

}
