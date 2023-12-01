package com.example.courseservice.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.example.courseservice.data.constants.TeacherIncomeStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Teacher_Income", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id","year","month"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherIncome {
    @Id
    @SequenceGenerator(name = "teacher_income_sequence", sequenceName = "teacher_income_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "teacher_income_sequence")
    private long id;

    private Double money;

    private Double receivedMoney;

    private Integer month;

    private Integer year;

    @Column(name = "user_id")
    private Long userId;

    private String BankCode;

    private LocalDateTime paymentDate;

    private String transactionCode;

    @Enumerated(EnumType.STRING)
    private TeacherIncomeStatus status;

    @ManyToOne()
    @JoinColumn(name = "course_id")
    private Course course;
}
