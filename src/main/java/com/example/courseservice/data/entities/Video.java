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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.constants.VideoStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Entity
@Table(name = "Video")
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

    @Column(columnDefinition = "TEXT")
    private String urlMaterial;

    @Default
    private LocalDateTime createdDate = LocalDateTime.now();

    private float duration;

    @Default
    private LocalDateTime updateTime= LocalDateTime.now();

    private String cloudinaryId;

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

    @OneToMany(mappedBy = "video")
    private List<Material> materials;

    @OneToOne()
    @JoinColumn(name = "video_id")
    private Video videoId;

    @OneToMany(mappedBy = "video")
    private List<StudentVideoProgress> studentVideoProgressList;

    @Transient
    public long getReaction(ReactStatus reactStatus) {
        return reactVideos.stream()
                .filter(reactVideo -> reactVideo.getReactStatus() == reactStatus)
                .count();
    }

}
