package com.example.courseservice.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Transaction;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);
}
