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
            "SIZE(c.videos) AS totalVideo, " +
            "c.subject AS subject, " +
            "c.level.name AS level, " +
            "c.price AS price, " +
            "c.createdDate AS createdDate, " +
            "c.updateTime AS updateDate, " +
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
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
            "c.commonStatus AS status " +
            "FROM Course c " +
            "LEFT JOIN c.ratings r " +
            "WHERE c.commonStatus <> :status " +
            "AND c.id NOT IN :excludedIds " +
            "GROUP BY c.id, c.level.name " +
            "ORDER BY averageRating DESC")
    Page<CourseResponseInterface> getAvailableCoursesByCommonStatusNotAndNotInList(
            @Param("status") CommonStatus status,
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
            "GROUP BY c.id, c.level.name")
    Page<CourseResponseInterface> getCourseByEmail(@Param("email") String email, Pageable pageable);

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
    Page<CourseResponseInterface> getCourseByEmail(
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

    @Query("SELECT " +
            "c.id AS id, " +
            "c.thumbnial AS thumbnial, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.teacherId AS teacherId, " +
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
            "WHERE ((:subjectIdList )IS NULL OR c.subjectId IN (:subjectIdList)) " +
            "AND (:minPrice IS NULL OR c.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR c.price <= :maxPrice) " +
            "AND ((:levelList )IS NULL OR c.level.id IN (:levelList)) " +
            "AND ((:topicList) IS NULL OR ct.topicId IN (:topicList)) " +
            "GROUP BY c.id, c.level.name " +
            "HAVING " +
            "(:minRate IS NULL OR COALESCE(AVG(r.rate), 0) >= :minRate) " +
            "AND (:maxRate IS NULL OR COALESCE(AVG(r.rate), 0) <= :maxRate) " +
            "ORDER BY averageRating DESC, createdDate DESC")
    Page<CourseResponseInterface> filterCourses(
            @Param("subjectIdList") List<Long> subjectIdList,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minRate") Double minRate,
            @Param("maxRate") Double maxRate,
            @Param("levelList") List<Long> levelList,
            @Param("topicList") List<Long> topicList,
            Pageable pageable);

    @Query("SELECT COUNT(DISTINCT sec.studentId) " +
            "FROM Course c " +
            "JOIN c.studentEnrolledCourses sec " +
            "WHERE c.teacherId = :teacherId")
    Long countStudentsEnrolledByTeacherId(@Param("teacherId") Long teacherId);
}
