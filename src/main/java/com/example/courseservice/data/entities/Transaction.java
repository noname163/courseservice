package com.example.courseservice.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.courseservice.data.constants.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Bank_Transaction")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @SequenceGenerator(name = "bank_transaction_sequence", sequenceName = "bank_transaction_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "bank_transaction_sequence")
    private long id;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private LocalDateTime paymentDate;

    private LocalDateTime expriedDate;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String vnpTxnRef;

    private String userEmail;

    private String userName;

    @Column(columnDefinition = "TEXT")
    private String userAvatar;

    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String refundEvidence;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    @Column(columnDefinition = "TEXT")
    private String studentNote;

    @ManyToOne
    private Course course;
}
