package com.example.courseservice.services.studentnoteservice.impl;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.dto.request.StudentNoteEdit;
import com.example.courseservice.data.dto.request.StudentNoteRequest;
import com.example.courseservice.data.dto.response.StudentNoteResponse;
import com.example.courseservice.data.entities.StudentNote;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.repositories.StudentNoteRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.StudentNoteMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.studentnoteservice.StudentNoteService;

@Service
public class StudentNoteServiceImpl implements StudentNoteService {

    @Autowired
    private StudentNoteRepository studentNoteRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private StudentNoteMapper studentNoteMapper;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public StudentNoteResponse createNote(StudentNoteRequest studentNoteRequest) {
        Long userId = securityContextService.getCurrentUser().getId();
        Video video = videoRepository.findById(studentNoteRequest.getVideoId()).orElseThrow(
                () -> new BadRequestException("Not found video with Id " + studentNoteRequest.getVideoId()));
        String[] time = studentNoteRequest.getDuration().split(":");
        int timeInSecond = Integer.parseInt(time[0])*60 + Integer.parseInt(time[1])*60 + Integer.parseInt(time[2]);
        if (video.getDuration() < timeInSecond) {
            throw new BadRequestException("Time of note must smaller than video duration");
        }
        List<StudentNote> studentNotes = studentNoteRepository.findByStudentIdAndVideoIdOrderByDurationAsc(userId,
                studentNoteRequest.getVideoId());
        if (studentNotes.size() > 5) {
            throw new BadRequestException("Only 5 note exist in 1 video");
        }
        StudentNote studentNote = studentNoteRepository.save(StudentNote
                .builder()
                .createdDate(LocalDateTime.now())
                .duration(studentNoteRequest.getDuration())
                .note(studentNoteRequest.getNote())
                .video(video)
                .studentId(userId)
                .build());

        return studentNoteMapper.mapToStudentNoteResponse(studentNote);
    }

    @Override
    public List<StudentNoteResponse> getNoteByVideoId(Long videoId) {
        Long userId = securityContextService.getCurrentUser().getId();
        List<StudentNote> studentNotes = studentNoteRepository.findByStudentIdAndVideoIdOrderByDurationAsc(userId, videoId);
        return studentNoteMapper.mapToStudentNoteResponseList(studentNotes);
    }

    @Override
    public StudentNoteResponse editStudentNote(StudentNoteEdit studentNoteEdit) {
        Long userId = securityContextService.getCurrentUser().getId();
        StudentNote note = studentNoteRepository.findByStudentIdAndId(userId, studentNoteEdit.getId())
                .orElseThrow(
                        () -> new BadRequestException("Not found student note with id " + studentNoteEdit.getId()));
        note.setNote(studentNoteEdit.getNote());
        note = studentNoteRepository.save(note);
        return studentNoteMapper.mapToStudentNoteResponse(note);
    }

    @Override
    public void deleteNote(Long noteId) {
        Long userId = securityContextService.getCurrentUser().getId();
        StudentNote note = studentNoteRepository.findByStudentIdAndId(userId, noteId)
                .orElseThrow(
                        () -> new BadRequestException("Not found student note with id " + noteId));
        studentNoteRepository.delete(note);
    }

}
