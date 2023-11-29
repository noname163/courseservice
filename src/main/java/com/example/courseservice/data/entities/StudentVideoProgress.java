package com.example.courseservice.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "StudentVideoProgress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentVideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    private boolean isCompleted;

    private LocalDateTime completionDate;

}
