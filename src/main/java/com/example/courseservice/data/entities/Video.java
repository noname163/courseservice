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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.constants.VideoStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Video",uniqueConstraints = @UniqueConstraint(columnNames = {"ordinalNumber", "course_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @SequenceGenerator(name = "video_sequence", sequenceName = "video_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "video_sequence")
    private long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String urlVideo;
    
    @Column(columnDefinition = "TEXT")
    private String urlThumbnail;

    private LocalDateTime createDate;

    private float duration;

    private LocalDateTime updateTime;

    @Enumerated(EnumType.STRING)
    private CommonStatus status;
    
    @Enumerated(EnumType.STRING)
    private VideoStatus videoStatus;

    private Integer ordinalNumber;

    @ManyToOne()
    private Course course;

    @OneToMany(mappedBy = "video")
    private List<ReactVideo> reactVideos;

    @OneToMany(mappedBy = "video")
    private List<StudentNote> studentNotes;

    @OneToMany(mappedBy = "video")
    private List<Comment> comments;

    @OneToMany(mappedBy = "video")
    private List<VideoUrl> videoUrls;

    @Transient
    public long getReaction(ReactStatus reactStatus) {
        return reactVideos.stream()
                .filter(reactVideo -> reactVideo.getReactStatus() == reactStatus)
                .count();
    }

}
