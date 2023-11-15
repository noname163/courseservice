package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.TeacherIncome;

public interface TeacherIncomeRepository extends JpaRepository<TeacherIncome, Long> {
    
}
