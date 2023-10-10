package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.Report;

public interface ReportRepository extends JpaRepository<Report, Long>{
    
}
