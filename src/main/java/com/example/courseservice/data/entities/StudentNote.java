package com.example.courseservice.data.entities;

import java.sql.Time;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.courseservice.data.constants.CommonStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Student_Note")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentNote {
    @Id
    @SequenceGenerator(name = "student_note_sequence", sequenceName = "student_note_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "student_note_sequence")
    private long id;

    @Column(columnDefinition = "TEXT")
    private String note;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private Time duration;

    private String studentEmail;

    private CommonStatus commonStatus;

    @ManyToOne()
    private Video video;
}
