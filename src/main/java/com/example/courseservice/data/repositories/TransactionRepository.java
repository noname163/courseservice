package com.example.courseservice.data.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.TransactionStatus;
import com.example.courseservice.data.entities.Transaction;
import com.example.courseservice.data.object.TransactionResponseInterface;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);

     @Query("SELECT t.createdDate AS createdDate, t.paymentDate AS paymentDate, " +
           "t.status AS transactionStatus, t.amount AS amount, " +
           "t.course.id AS courseId, t.course.name AS courseName " +
           "FROM Transaction t " +
           "WHERE t.userId = :userId")
    Page<TransactionResponseInterface> findTransactionsByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT t.createdDate AS createdDate, t.paymentDate AS paymentDate, " +
           "t.status AS transactionStatus, t.amount AS amount, " +
           "t.course.id AS courseId, t.course.name AS courseName " +
           "FROM Transaction t " +
           "WHERE (:status IS NULL OR t.status = :status)")
    Page<TransactionResponseInterface> findTransactionsByStatus(
            @Param("status") TransactionStatus status,
            Pageable pageable
    );
}
