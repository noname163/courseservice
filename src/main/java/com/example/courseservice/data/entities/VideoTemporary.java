package com.example.courseservice.data.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.VideoStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Video_Temporary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTemporary {
    @Id
    @SequenceGenerator(name = "video_temporary_sequence", sequenceName = "video_temporary_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "video_temporary_sequence")
    private long id;

    @ManyToOne()
    private Video video;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String urlVideo;

    @Column(columnDefinition = "TEXT")
    private String urlThumbnail;

    @Column(columnDefinition = "TEXT")
    private String urlMaterial ;
    
    private float duration;

    private LocalDateTime updateTime;

    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private VideoStatus videoStatus;

    private Integer ordinalNumber;

    @Enumerated(EnumType.STRING)
    private CommonStatus status;

    @ManyToOne()
    private Course course;

    @ManyToOne()
    private CourseTemporary courseTemporary;

}
