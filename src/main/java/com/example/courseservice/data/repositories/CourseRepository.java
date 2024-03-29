package com.example.courseservice.data.repositories;

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

    public Optional<Course> findCourseByCourseId(Course course);

    public List<Course> findCourseByTeacherEmail(String email);

    public Long countByTeacherId(Long id);

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

    public List<Course> findByIdInAndCommonStatusAndTeacherId(Set<Long> ids, CommonStatus commonStatus,
            Long teacherId);

    public Optional<Course> findByIdAndCommonStatus(Long id, CommonStatus commonStatus);

    public Page<Course> findByCommonStatusAndIdNotIn(CommonStatus commonStatus, List<Long> courseIds,
            Pageable pageable);

    public Optional<Course> findByIdAndCommonStatusNot(Long id, CommonStatus commonStatus);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.commonStatus = :status " +
            "GROUP BY c.id, c.level.name " +
            "ORDER BY averageRating DESC")
    Page<CourseResponseInterface> getByCommonStatusJPQL(@Param("status") CommonStatus status, Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "COUNT(v) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "LEFT JOIN c.videos v WITH v.status = 'AVAILABLE' " +
            "GROUP BY c.id, c.level.name " +
            "ORDER BY averageRating DESC")
    Page<CourseResponseInterface> findByAllCommonStatus(Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status, " +
            "CASE WHEN c.id IN :excludedIds THEN true ELSE false END AS isAccess " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.commonStatus NOT IN :status " +
            "AND (c.id NOT IN :excludedIds OR c.id IS NULL) " +
            "GROUP BY c.id, c.level.name " +
            "ORDER BY averageRating DESC")
    Page<CourseResponseInterface> getAvailableCoursesByCommonStatusNotAndNotInList(
            @Param("status") List<CommonStatus> status,
            @Param("excludedIds") List<Long> excludedIds,
            Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.teacherEmail = :email " +
            "AND (:statuses IS NULL OR c.commonStatus IN :statuses) " +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getCourseByEmail(
            @Param("email") String email,
            @Param("statuses") List<CommonStatus> statuses,
            Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status, " +
            "CASE WHEN c.id IN :enrolledIds THEN true ELSE false END AS isAccess " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.teacherEmail = :email " +
            "AND c.commonStatus = :status " +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getCourseByEmailForUser(
            @Param("email") String email,
            @Param("enrolledIds") List<Long> enrolledIds,
            @Param("status") CommonStatus status,
            Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.teacherEmail = :email " +
            "AND c.commonStatus = :status " +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getCourseByEmailAndStatus(@Param("email") String email,
            @Param("status") CommonStatus commonStatus, Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.teacherEmail = :email " +
            "AND c.commonStatus <> :status " +
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getCourseByEmailAndStatusNot(@Param("email") String email,
            @Param("status") CommonStatus commonStatus, Pageable pageable);

    @Query("SELECT " +
            "c.id AS id, " +
            "COUNT(DISTINCT sec.studentEmail) AS totalStudent, " +
            "c.description AS description, " +
            "c.thumbnial AS thumbnail, " +
            "c.teacherName AS teacherName, " +
            "c.name AS name, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.teacherEmail AS teacherEmail, " +
            "c.teacherId AS teacherId, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.createdDate AS createdDate, " +
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

    @Query("SELECT " +
            "c.id AS id, " +
            "COUNT(DISTINCT sec.studentEmail) AS totalStudent, " +
            "c.description AS description, " +
            "c.thumbnial AS thumbnail, " +
            "c.teacherName AS teacherName, " +
            "c.teacherEmail AS teacherEmail, " +
            "c.teacherId AS teacherId, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS name, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "LEFT JOIN c.videos v " +
            "LEFT JOIN c.studentEnrolledCourses sec ON sec.course.id = c.id " +
            "WHERE c.id = :id " +
            "AND c.commonStatus <> :status " +
            "GROUP BY c.id, c.level.name")
    CourseDetailResponseInterface getCourseDetailsByCourseIdAndStatusNot(@Param("id") Long id,
            @Param("status") CommonStatus commonStatus);

    @Query("SELECT " +
            "c.id AS id, " +
            "COUNT(DISTINCT sec.studentEmail) AS totalStudent, " +
            "c.description AS description, " +
            "c.thumbnial AS thumbnail, " +
            "c.teacherName AS teacherName, " +
            "c.teacherEmail AS teacherEmail, " +
            "c.teacherId AS teacherId, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS name, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "LEFT JOIN c.videos v " +
            "LEFT JOIN c.studentEnrolledCourses sec ON sec.course.id = c.id " +
            "WHERE c.id = :id " +
            "GROUP BY c.id, c.level.name")
    CourseDetailResponseInterface getCourseDetailsByCourseIdIgnoreStatus(@Param("id") Long id);

    @Query("SELECT c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "LEFT JOIN c.courseTopics ct " +
            "WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "   OR LOWER(c.teacherName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "   OR LOWER(c.teacherEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "   OR EXISTS (SELECT 1 FROM c.courseTopics ct WHERE LOWER(ct.topicName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            +
            "   OR LOWER(c.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND c.commonStatus = 'AVAILABLE' " +
            "GROUP BY c.id, c.thumbnial, c.teacherName, c.name, c.subject, c.level.name, c.price, c.createdDate, c.updateTime, c.commonStatus")
    Page<CourseResponseInterface> searchCourses(
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    @Query("SELECT COUNT(DISTINCT sec.studentId) " +
            "FROM Course c " +
            "JOIN c.studentEnrolledCourses sec " +
            "WHERE c.teacherId = :teacherId")
    Long countStudentsEnrolledByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT c.id AS id, " +
            "c.id AS courseRealId, " +
            "c.teacherId AS teacherId, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.teacherId = :teacherId " +
            "AND (:name is null or lower(c.name) like lower(concat('%', :name, '%'))) " +
            "AND (:status = 'ALL' or c.commonStatus = :status) " +
            "GROUP BY c.id, c.teacherId, c.thumbnial, c.teacherName, c.teacherAvatar, " +
            "c.name, c.subject, c.level.name, c.price, c.createdDate, c.updateTime, " +
            "c.commonStatus")
    Page<CourseResponseInterface> searchCoursesForTeacher(
            @Param("teacherId") Long teacherId,
            @Param("name") String name,
            @Param("status") String status,
            Pageable pageable);

    @Query("SELECT c.id AS id, " +
            "c.id AS courseRealId, " +
            "c.teacherId AS teacherId, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.name AS courseName, " +
            "SIZE(c.ratings) AS numberOfRate, " +
            "SIZE(c.videos) AS totalVideo, " +
            "COALESCE(AVG(r.rate), 0) AS averageRating, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE (:name is null or lower(c.name) like lower(concat('%', :name, '%'))) " +
            "AND (:status IS NULL or c.commonStatus IN :status) " +
            "GROUP BY c.id, c.thumbnial, c.teacherName, c.teacherAvatar, " +
            "c.name, c.subject, c.level.name, c.price, c.createdDate, c.updateTime, " +
            "c.commonStatus")
    Page<CourseResponseInterface> searchCoursesForAdmin(
            @Param("name") String name,
            @Param("status") List<CommonStatus> status,
            Pageable pageable);
}
