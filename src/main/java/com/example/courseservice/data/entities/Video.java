package com.example.courseservice.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.courseservice.data.constants.VideoStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Video")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @SequenceGenerator(name = "video_sequence", sequenceName = "video_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "video_sequence")
    private long id;

    private String url;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private VideoStatus status;

    @ManyToOne()
    private Course course;
}
