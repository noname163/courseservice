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

public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByCourseAndStatusOrderByOrdinalNumberAsc(Course course, CommonStatus status, Pageable pageable);

    Page<Video> findByStatusOrderByOrdinalNumberAsc(CommonStatus status, Pageable pageable);

    Page<Video> findByStatusAndCourseInOrderByOrdinalNumberAsc(CommonStatus status, List<Course> courses,
            Pageable pageable);

    Page<Video> findByCourseIdOrderByOrdinalNumberAsc(Long courseId, Pageable pageable);

    Page<Video> findByCourseInOrderByOrdinalNumberAsc(List<Course> courses, Pageable pageable);

    Page<Video> findByCourseInAndStatusNotOrderByOrdinalNumberAsc(List<Course> courses, CommonStatus commonStatus,
            Pageable pageable);

    List<Video> findByCourseAndStatusOrderByOrdinalNumberAsc(Course course, CommonStatus status);

    List<Video> findByCourseIdAndStatusOrderByOrdinalNumberAsc(Long courseId, CommonStatus status);

    List<Video> findByCourseIdAndStatusNotOrderByOrdinalNumberAsc(Long courseId, CommonStatus status);

    List<Video> findByCourseAndStatusNotOrderByOrdinalNumberAsc(Course course, CommonStatus status);

    List<Video> findByCourseIdAndIdInOrderByOrdinalNumberAsc(Long courseId, Set<Long> ids);

    Optional<Video> findByIdAndStatusOrderByOrdinalNumberAsc(Long id, CommonStatus status);

    Optional<Video> findByIdAndStatusNotOrderByOrdinalNumberAsc(Long id, CommonStatus status);

    @Query("SELECT MAX(v.ordinalNumber) FROM Video v WHERE v.course = :course")
    Integer findMaxOrdinalNumberByCourse(@Param("course") Course course);

    @Query("SELECT " +
            "v.id AS id, " +
            "v.name AS name, " +
            "v.urlThumbnail AS thumbnail, " +
            "v.status AS status, " +
            "v.urlVideo AS url, " +
            "v.duration AS duration, " +
            "v.videoStatus AS videoStatus, " +
            "COUNT(CASE WHEN rv.reactStatus = com.example.courseservice.data.constants.ReactStatus.LIKE THEN 1 ELSE null END) AS totalLike, "
            +
            "COUNT(c.id) AS totalComment, " +
            "FALSE AS isDraft, " +
            "v.ordinalNumber AS ordinalNumber " +
            "FROM Video v " +
            "LEFT JOIN v.reactVideos rv " +
            "LEFT JOIN v.comments c " +
            "WHERE v.course.id = :courseId " +
            "AND v.status = :status " +
            "GROUP BY v.id, v.name, v.urlThumbnail, v.duration, v.ordinalNumber " +
            "ORDER BY v.ordinalNumber ASC")
    List<CourseVideoResponseInterface> getCourseVideosByCourseIdAndCommonStatus(@Param("courseId") Long courseId,
            @Param("status") CommonStatus commonStatus);

    @Query("SELECT " +
            "v.id AS id, " +
            "v.name AS name, " +
            "v.urlThumbnail AS thumbnail, " +
            "v.status AS status, " +
            "v.urlVideo AS url, " +
            "v.duration AS duration, " +
            "COUNT(CASE WHEN rv.reactStatus = com.example.courseservice.data.constants.ReactStatus.LIKE THEN 1 ELSE null END) AS totalLike, "
            +
            "COUNT(c.id) AS totalComment, " +
            "v.ordinalNumber AS ordinalNumber, " +
            "v.videoStatus AS videoStatus " +
            "FROM Video v " +
            "LEFT JOIN v.reactVideos rv " +
            "LEFT JOIN v.comments c " +
            "WHERE v.course.id = :courseId " +
            "AND v.status <> :status " +
            "GROUP BY v.id, v.name, v.urlThumbnail, v.duration, v.ordinalNumber " +
            "ORDER BY v.ordinalNumber ASC")
    List<CourseVideoResponseInterface> getCourseVideosByCourseIdAndCommonStatusNot(@Param("courseId") Long courseId,
            @Param("status") CommonStatus commonStatus);

    @Query("SELECT COUNT(v) FROM Video v " +
            "WHERE v.course.teacherId = :teacherId")
    Long countVideosByTeacherId(@Param("teacherId") Long teacherId);

}
