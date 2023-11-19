package com.example.courseservice.data.repositories;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Level;
import com.example.courseservice.data.object.CourseDetailResponseInterface;
import com.example.courseservice.data.object.CourseResponseInterface;

@Repository
@EnableJpaRepositories
public interface CourseRepository extends JpaRepository<Course, Long> {
    public Page<Course> findCourseByTeacherEmailAndCommonStatus(String email, CommonStatus commonStatus,
            Pageable pageable);

    public Page<Course> findCourseByTeacherEmail(String email, Pageable pageable);

    public Optional<Course> findCourseByTeacherEmailAndId(String email, Long id);

    public List<Course> findCourseByTeacherEmail(String email);

    public Boolean existsByTeacherEmailAndId(String email, Long courseId);

    public Page<Course> findByCommonStatusAndSubjectIn(Pageable pageable, CommonStatus commonStatus,
            List<String> subjectName);

    @Query("SELECT c FROM Course c WHERE c.commonStatus = :commonStatus AND c.price BETWEEN :min AND :max")
    Page<Course> findByCommonStatusAndPriceBetween(
            @Param("commonStatus") CommonStatus commonStatus,
            @Param("min") Double min,
            @Param("max") Double max,
            Pageable pageable);

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.commonStatus = :commonStatus " +
            "GROUP BY c.id " +
            "HAVING AVG(r.rate) BETWEEN :minRate AND :maxRate")
    Page<Course> findByCommonStatusAndAverageRateBetween(
            @Param("commonStatus") CommonStatus commonStatus,
            @Param("minRate") Double minRate,
            @Param("maxRate") Double maxRate,
            Pageable pageable);

    public Page<Course> findByCommonStatusAndLevelIn(Pageable pageable, CommonStatus commonStatus,
            List<Level> level);

    public List<Course> findByIdInAndCommonStatus(Set<Long> ids, CommonStatus commonStatus);

    public Optional<Course> findByIdAndCommonStatus(Long id, CommonStatus commonStatus);

    public Page<Course> findByCommonStatusAndIdNotIn(CommonStatus commonStatus, List<Long> courseIds,
            Pageable pageable);

    public Optional<Course> findByIdAndCommonStatusNot(Long id, CommonStatus commonStatus);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.commonStatus = :status " +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getByCommonStatusJPQL(@Param("status") CommonStatus status, Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> findByAllCommonStatus(Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.commonStatus = :status " +
            "AND c.id NOT IN :excludedIds " +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getAvailableCoursesByCommonStatusAndNotInList(
            @Param("status") CommonStatus status,
            @Param("excludedIds") List<Long> excludedIds,
            Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.teacherEmail = :email " +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getCourseByEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT " +
            "c.id AS id, " +
            "COUNT(DISTINCT sec.studentEmail) AS totalStudent, " +
            "c.description AS description, " +
            "c.thumbnial AS thumbnail, " +
            "c.teacherName AS teacherName, "+
            "c.name AS name, "+
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "LEFT JOIN c.videos v " +
            "LEFT JOIN c.studentEnrolledCourses sec ON sec.course.id = c.id " +
            "WHERE c.id = :id " +
            "AND c.commonStatus = :status " +
            "GROUP BY c.id, c.level.name")
    CourseDetailResponseInterface getCourseDetailsByCourseId(@Param("id") Long id,
            @Param("status") CommonStatus commonStatus);

}
