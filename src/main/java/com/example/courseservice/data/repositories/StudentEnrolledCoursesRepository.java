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
import com.example.courseservice.data.entities.StudentEnrolledCourses;

public interface StudentEnrolledCoursesRepository extends JpaRepository<StudentEnrolledCourses, Long> {
    Page<StudentEnrolledCourses> findByStudentEmail(String email, Pageable pageable);

    List<StudentEnrolledCourses> findByStudentEmail(String email);

    Optional<StudentEnrolledCourses> findByStudentEmailAndCourseId(String email, Long courseId);

    Optional<StudentEnrolledCourses> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT c.id FROM Course c " +
            "WHERE c IN :courses " + // Filter courses from the provided list
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM StudentEnrolledCourses sec " +
            "   WHERE sec.studentEmail = :studentEmail " +
            "   AND sec.course.id = c.id)")
    List<Long> filterCourseStudentNotEnrolled(
            @Param("studentEmail") String studentEmail,
            @Param("courses") List<Course> courses);

    @Query("SELECT DISTINCT v.id FROM Video v " +
            "JOIN v.course c " +
            "JOIN c.studentEnrolledCourses sec " +
            "WHERE sec.studentEmail = :studentEmail " +
            "AND c IN :courses " +
            "AND v.status = :status")
    List<Long> getVideoIdsByStudentEmailAndCourses(
            @Param("studentEmail") String studentEmail,
            @Param("courses") List<Course> courses,
            @Param("status") CommonStatus status);

}
