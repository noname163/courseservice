package com.example.courseservice.data.repositories;

import java.util.HashSet;
import java.util.List;
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

@Repository
@EnableJpaRepositories
public interface CourseRepository extends JpaRepository<Course, Long> {
    public Page<Course> findCourseByTeacherEmailAndCommonStatus(String email, CommonStatus commonStatus,
            Pageable pageable);

    public Page<Course> findCourseByTeacherEmail(String email, Pageable pageable);

    public Page<Course> findByCommonStatus(Pageable pageable, CommonStatus commonStatus);

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
            "HAVING AVG(r.rate) IN :rates")
    Page<Course> findByCommonStatusAndAverageRateBetween(
            @Param("commonStatus") CommonStatus commonStatus,
            @Param("rates") List<Float> rate,
            Pageable pageable);

    public Page<Course> findByCommonStatusAndLevelIn(Pageable pageable, CommonStatus commonStatus, List<Level> level);

    public List<Course> findByIdInAndCommonStatus(Set<Long> ids, CommonStatus commonStatus);

}
