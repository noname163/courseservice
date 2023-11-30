package com.example.courseservice.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.example.courseservice.data.constants.ReactStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "React_Video",uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "student_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactVideo {
    @Id
    @SequenceGenerator(name = "react_video_sequence", sequenceName = "react_video_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "react_video_sequence")
    private long id;

    private String studentName;

    @Column(name = "student_id")
    private Long studentId;

    private LocalDateTime createdDate;

    private LocalDateTime updateTime;

    private ReactStatus reactStatus;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
}
