package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.CourseVideoResponseInterface;
import com.example.courseservice.data.object.VideoItemResponseInterface;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByCourseAndStatus(Course course, CommonStatus status, Pageable pageable);

    Page<Video> findByStatus(CommonStatus status, Pageable pageable);

    Page<Video> findByStatusAndCourseIn(CommonStatus status, List<Course> courses, Pageable pageable);

    Page<Video> findByCourseIn(List<Course> courses, Pageable pageable);

    List<Video> findByCourseAndStatus(Course course, CommonStatus status);

    List<Video> findByCourseIdAndStatus(Long courseId, CommonStatus status);

    List<Video> findByCourseIdAndStatusNot(Long courseId, CommonStatus status);

    List<Video> findByCourseAndStatusNot(Course course, CommonStatus status);

    List<Video> findByCourseIdAndIdIn(Long courseId, Set<Long> ids);

    Optional<Video> findByIdAndStatus(Long id, CommonStatus status);

    Optional<Video> findByIdAndStatusNot(Long id, CommonStatus status);

    @Query("SELECT MAX(v.ordinalNumber) FROM Video v WHERE v.course = :course")
    Integer findMaxOrdinalNumberByCourse(@Param("course") Course course);

    @Query("SELECT " +
            "v.id AS id, " +
            "v.name AS name, " +
            "v.urlThumbnail AS thumbnail, " +
            "v.duration AS duration, " +
            "COUNT(rv.id) AS totalLike, " +
            "COUNT(c.id) AS totalComment, " +
            "v.ordinalNumber AS ordinalNumber " +
            "FROM Video v " +
            "LEFT JOIN v.reactVideos rv " +
            "LEFT JOIN v.comments c " +
            "WHERE v.course.id = :courseId " +
            "AND v.status = :status "+
            "GROUP BY v.id, v.name, v.urlThumbnail, v.duration, v.ordinalNumber")
    List<CourseVideoResponseInterface> getCourseVideosByCourseIdAndCommonStatus(@Param("courseId") Long courseId, @Param("status") CommonStatus commonStatus);
    @Query("SELECT " +
            "v.id AS id, " +
            "v.name AS name, " +
            "v.urlThumbnail AS thumbnail, " +
            "v.duration AS duration, " +
            "COUNT(rv.id) AS totalLike, " +
            "COUNT(c.id) AS totalComment, " +
            "v.ordinalNumber AS ordinalNumber " +
            "FROM Video v " +
            "LEFT JOIN v.reactVideos rv " +
            "LEFT JOIN v.comments c " +
            "WHERE v.course.id = :courseId " +
            "AND v.status != :status "+
            "GROUP BY v.id, v.name, v.urlThumbnail, v.duration, v.ordinalNumber")
    List<CourseVideoResponseInterface> getCourseVideosByCourseIdAndCommonStatusNot(@Param("courseId") Long courseId, @Param("status") CommonStatus commonStatus);
}
