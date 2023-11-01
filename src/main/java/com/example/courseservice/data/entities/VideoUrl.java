package com.example.courseservice.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Video_Url")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUrl {
    @Id
    @SequenceGenerator(name = "video_url_sequence", sequenceName = "video_url_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "video_url_sequence")
    private long id;
    
    @Column(columnDefinition = "TEXT")
    private String url;

    private Integer startTime;

    private Integer endTime;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    @ManyToOne()
    private Video video;
}
