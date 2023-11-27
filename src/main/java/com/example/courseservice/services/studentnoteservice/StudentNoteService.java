package com.example.courseservice.services.studentnoteservice;

import java.util.List;

import com.example.courseservice.data.dto.request.StudentNoteEdit;
import com.example.courseservice.data.dto.request.StudentNoteRequest;
import com.example.courseservice.data.dto.response.StudentNoteResponse;

public interface StudentNoteService {
    public StudentNoteResponse createNote(StudentNoteRequest studentNoteRequest);

    public List<StudentNoteResponse> getNoteByVideoId(Long videoId);

    public StudentNoteResponse editStudentNote(StudentNoteEdit studentNoteEdit);

    public void deleteNote(Long noteId);
}
