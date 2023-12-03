package com.example.courseservice.data.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.TransactionStatus;
import com.example.courseservice.data.entities.Transaction;
import com.example.courseservice.data.object.TransactionByMonthInterface;
import com.example.courseservice.data.object.TransactionResponseInterface;

@EnableJpaRepositories
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);

    @Query("SELECT t.createdDate AS createdDate, t.paymentDate AS paymentDate, " +
            "t.id AS id, " +
            "t.status AS transactionStatus, t.amount AS amount, " +
            "t.course.id AS courseId, t.course.name AS courseName, " +
            "t.course.teacherName AS teacherName, " +
            "t.course.teacherAvatar AS teacherAvatar, " +
            "t.course.subject AS subject, " +
            "t.vnpTxnRef AS transactionCode " +
            "FROM Transaction t " +
            "WHERE t.userId = :userId")
    Page<TransactionResponseInterface> findTransactionsByUserId(
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("SELECT t.createdDate AS createdDate, t.paymentDate AS paymentDate, " +
            "t.id AS id, " +
            "t.status AS transactionStatus, t.amount AS amount, " +
            "t.course.id AS courseId, t.course.name AS courseName, " +
            "t.course.teacherName AS teacherName, " +
            "t.course.teacherAvatar AS teacherAvatar, " +
            "t.course.subject AS subject, " +
            "t.vnpTxnRef AS transactionCode " +
            "FROM Transaction t " +
            "WHERE t.userId = :userId " +
            "AND t.status = :status")
    Page<TransactionResponseInterface> findTransactionsByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") TransactionStatus status,
            Pageable pageable);

    @Query("SELECT t.createdDate AS createdDate, t.paymentDate AS paymentDate, " +
            "t.id AS id, " +
            "t.status AS transactionStatus, t.amount AS amount, " +
            "t.userName AS userName, " +
            "t.userAvatar AS userAvatar, " +
            "t.course.id AS courseId, t.course.name AS courseName, " +
            "t.course.teacherName AS teacherName, " +
            "t.course.teacherAvatar AS teacherAvatar, " +
            "t.course.subject AS subject, " +
            "t.vnpTxnRef AS transactionCode " +
            "FROM Transaction t " +
            "WHERE (:status IS NULL OR t.status = :status)")
    Page<TransactionResponseInterface> findTransactionsByStatus(
            @Param("status") TransactionStatus status,
            Pageable pageable);

    @Query("SELECT " +
            "t.id AS id, " +
            "t.vnpTxnRef AS transactionCode, " +
            "t.createdDate AS createdDate, " +
            "t.paymentDate AS paymentDate, " +
            "c.subject AS subject, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "t.userName AS userName, " +
            "t.userAvatar AS userAvatar, " +
            "t.status AS transactionStatus, " +
            "t.amount AS amount, " +
            "c.id AS courseId, " +
            "c.name AS courseName " +
            "FROM Transaction t " +
            "JOIN t.course c " +
            "WHERE c.teacherId = :teacherId " +
            "ORDER BY t.createdDate DESC")
    Page<TransactionResponseInterface> getTransactionsByTeacherId(
            @Param("teacherId") Long teacherId,
            Pageable pageable);

    @Query("SELECT " +
            "t.id AS id, " +
            "t.vnpTxnRef AS transactionCode, " +
            "t.createdDate AS createdDate, " +
            "t.paymentDate AS paymentDate, " +
            "c.subject AS subject, " +
            "c.teacherName AS teacherName, " +
            "c.teacherAvatar AS teacherAvatar, " +
            "t.userName AS userName, " +
            "t.userAvatar AS userAvatar, " +
            "t.status AS transactionStatus, " +
            "t.amount AS amount, " +
            "c.id AS courseId, " +
            "c.name AS courseName " +
            "FROM Transaction t " +
            "JOIN t.course c " +
            "WHERE c.teacherId = :teacherId " +
            "AND t.status = :status " +
            "ORDER BY t.createdDate DESC")
    Page<TransactionResponseInterface> getTransactionsByTeacherIdAndStatus(
            @Param("teacherId") Long teacherId,
            @Param("status") TransactionStatus status,
            Pageable pageable);

    @Query("SELECT " +
            "SUM(t.amount) AS amount, " +
            "CONCAT(EXTRACT(MONTH FROM t.paymentDate), '/', EXTRACT(YEAR FROM t.paymentDate)) AS monthOfYear " +
            "FROM Transaction t " +
            "WHERE EXTRACT(YEAR FROM t.paymentDate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND t.status = 'SUCCESS' " +
            "GROUP BY t.paymentDate " +
            "HAVING COUNT(DISTINCT EXTRACT(MONTH FROM t.paymentDate)) = 1 " +
            "ORDER BY EXTRACT(YEAR FROM t.paymentDate) DESC, EXTRACT(MONTH FROM t.paymentDate) DESC")
    List<TransactionByMonthInterface> getTransactionsByMonth();

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) " +
            "FROM Transaction t " +
            "WHERE EXTRACT(MONTH FROM t.paymentDate) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM t.paymentDate) = EXTRACT(YEAR FROM CURRENT_DATE)")
    Double getIncomeOfCurrentMonth();
}
