package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.entities.TeacherIncome;
import com.example.courseservice.data.object.CourseReportInterface;

public interface TeacherIncomeRepository extends JpaRepository<TeacherIncome, Long> {

    Optional<TeacherIncome> findByCourseIdAndUserIdAndMonthAndYear(Long courseId, Long UserId, Integer month,
            Integer year);

    @Query("SELECT t.course.id AS courseId, c.name AS courseName, CONCAT(t.month, '-', t.year) AS monthOfYear, COALESCE(SUM(t.money), 0) AS revenue "
            +
            "FROM TeacherIncome t " +
            "JOIN Course c ON t.course.id = c.id " +
            "WHERE c.teacherId = :userId " +
            "AND t.year = YEAR(CURRENT_DATE) " +
            "AND t.month BETWEEN MONTH(CURRENT_DATE) - 9 AND MONTH(CURRENT_DATE) " +
            "GROUP BY c.name, t.course.id, t.month, t.year")
    List<CourseReportInterface> getRevenueByTeacherEmailForLast10Months(@Param("userId") Long userId);

    @Query("SELECT t.course.id AS courseId, c.name AS courseName, CONCAT(t.month, '-', t.year) AS monthOfYear, SUM(t.money) AS revenue "
            +
            "FROM TeacherIncome t " +
            "JOIN Course c ON t.course.id = c.id " +
            "WHERE c.teacherId = :userId " +
            "AND c.id = :courseId " +
            "AND t.year = YEAR(CURRENT_DATE) " +
            "AND t.month BETWEEN MONTH(CURRENT_DATE) - 9 AND MONTH(CURRENT_DATE) " +
            "GROUP BY c.name, t.course.id, t.month, t.year")
    List<CourseReportInterface> getRevenueByTeacherEmailAndCourseIdForLast10Months(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);
}
