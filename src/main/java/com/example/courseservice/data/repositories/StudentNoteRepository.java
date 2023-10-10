package com.example.courseservice.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.StudentNote;

public interface StudentNoteRepository extends JpaRepository<StudentNote, Long> {
    
}
