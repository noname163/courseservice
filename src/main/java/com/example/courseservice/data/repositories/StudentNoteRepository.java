package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseservice.data.entities.StudentNote;

public interface StudentNoteRepository extends JpaRepository<StudentNote, Long> {
    List<StudentNote> findByStudentIdAndVideoIdOrderByDurationAsc(Long studentId, Long videoId);

    Optional<StudentNote> findByStudentIdAndId(Long userId, Long id);
}
