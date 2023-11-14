package com.example.courseservice.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.courseservice.data.constants.TeacherIncomeStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Teacher_Income")
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

    private Integer month;

    private Integer year;

    private Long userId;

    private TeacherIncomeStatus status;
}
