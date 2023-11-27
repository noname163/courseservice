package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.StudentNoteResponse;
import com.example.courseservice.data.entities.StudentNote;

@Component
public class StudentNoteMapper {
    public StudentNoteResponse mapToStudentNoteResponse(StudentNote studentNote) {
        if (studentNote == null) {
            return null;
        }

        return StudentNoteResponse.builder()
                .id(studentNote.getId())
                .note(studentNote.getNote())
                .duration(studentNote.getDuration())
                .createDate(studentNote.getCreateDate())
                .build();
    }

    public List<StudentNoteResponse> mapToStudentNoteResponseList(
            List<StudentNote> studentNoteResponseInterfaces) {
        return studentNoteResponseInterfaces.stream()
                .map(this::mapToStudentNoteResponse)
                .collect(Collectors.toList());
    }
}
