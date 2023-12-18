package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.TeacherIncomeStatus;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.TeacherIncome;
import com.example.courseservice.data.object.CourseReportInterface;
import com.example.courseservice.data.object.CourseRevenueByMonthInterface;

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

    @Query("SELECT " +
            "CONCAT(ti.month, '/', ti.year) AS month, " +
            "ti.id AS id, " +
            "SUM(ti.money) AS revenue " +
            "FROM TeacherIncome ti " +
            "WHERE ti.userId = :userId " +
            "AND ti.year = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND ti.month BETWEEN EXTRACT(MONTH FROM CURRENT_DATE) - 9 AND EXTRACT(MONTH FROM CURRENT_DATE) "
            +
            "GROUP BY ti.month, ti.id")
    List<CourseRevenueByMonthInterface> getTeacherRevenueByMonth(@Param("userId") Long userId);

    @Query("SELECT " +
            "t.id AS id, " +
            "c.id AS courseId, " +
            "c.name AS courseName, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.subject AS subject, " +
            "CONCAT(t.month, '/', t.year) AS monthOfYear, " +
            "t.money AS revenue, " +
            "t.receivedMoney AS receivedMoney, " +
            "t.paymentDate AS paymentDate, " +
            "t.status AS teacherIncomeStatus " +
            "FROM TeacherIncome t " +
            "JOIN t.course c " +
            "GROUP BY c.id, c.name, CONCAT(t.month, '/', t.year), t.money, t.status,t.paymentDate,t.receivedMoney, t.year, t.month, t.id, c.teacherName, c.teacherAvatar, c.subject "
            +
            "ORDER BY t.year DESC, t.month DESC")
    Page<CourseReportInterface> getCourseReportsOrderByMonthAndYear(Pageable pageable);

    @Query("SELECT " +
            "t.id AS id, " +
            "c.id AS courseId, " +
            "c.name AS courseName, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.subject AS subject, " +
            "CONCAT(t.month, '/', t.year) AS monthOfYear, " +
            "t.money AS revenue, " +
            "t.receivedMoney AS receivedMoney, " +
            "t.paymentDate AS paymentDate, " +
            "t.status AS teacherIncomeStatus " +
            "FROM TeacherIncome t " +
            "JOIN t.course c " +
            "WHERE t.status = :status " +
            "GROUP BY c.id, c.name, CONCAT(t.month, '/', t.year), t.money, t.status,t.paymentDate,t.receivedMoney, t.year, t.month, t.id,c.teacherName, c.teacherAvatar, c.subject  "
            +
            "ORDER BY t.year DESC, t.month DESC")
    Page<CourseReportInterface> getCourseReportsOrderByMonthAndYearByStatus(@Param("status") TeacherIncomeStatus status,
            Pageable pageable);

    @Query("SELECT " +
            "t.id AS id, " +
            "c.id AS courseId, " +
            "c.name AS courseName, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.subject AS subject, " +
            "CONCAT(t.month, '/', t.year) AS monthOfYear, " +
            "t.money AS revenue, " +
            "t.status AS teacherIncomeStatus, " +
            "t.paymentDate AS paymentDate, " +
            "t.receivedMoney AS receivedMoney " +
            "FROM TeacherIncome t " +
            "JOIN t.course c " +
            "WHERE t.status = :status " +
            "AND t.userId = :teacherId " +
            "AND t.receivedMoney IS NOT NULL "+
            "GROUP BY c.id, c.name, CONCAT(t.month, '/', t.year), t.money, t.status,t.paymentDate,t.receivedMoney, t.year, t.month,t.id, c.teacherName, c.teacherAvatar, c.subject "
            +
            "ORDER BY t.year DESC, t.month DESC")
    Page<CourseReportInterface> getCourseReportsOrderByMonthAndYearByStatusForTeacher(
            @Param("teacherId") Long teacherId, @Param("status") TeacherIncomeStatus status, Pageable pageable);

    @Query("SELECT " +
            "t.id AS id, " +
            "c.id AS courseId, " +
            "c.name AS courseName, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "c.subject AS subject, " +
            "CONCAT(t.month, '/', t.year) AS monthOfYear, " +
            "t.money AS revenue, " +
            "t.status AS teacherIncomeStatus, " +
            "t.paymentDate AS paymentDate, " +
            "t.receivedMoney AS receivedMoney " +
            "FROM TeacherIncome t " +
            "JOIN t.course c " +
            "WHERE t.userId = :teacherId " +
            "AND t.receivedMoney IS NOT NULL "+
            "GROUP BY c.id, c.name, CONCAT(t.month, '/', t.year), t.money, t.status, t.paymentDate,t.receivedMoney, t.year, t.month, t.id,c.teacherName, c.teacherAvatar, c.subject "
            +
            "ORDER BY t.year DESC, t.month DESC")
    Page<CourseReportInterface> getCourseReportsOrderByMonthAndYearForTeacher(
            @Param("teacherId") Long teacherId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(ti.receivedMoney), 0) " +
            "FROM TeacherIncome ti " +
            "WHERE ti.userId = :userId " +
            "AND EXTRACT(YEAR FROM ti.paymentDate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM ti.paymentDate) = EXTRACT(MONTH FROM CURRENT_DATE) ")
    Double getTotalIncomeByUserIdAndCurrentMonth(@Param("userId") Long userId);

    Optional<TeacherIncome> findByCourseAndMonthAndYear(Course course, Integer month, Integer year);

}
