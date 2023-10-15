package com.example.courseservice.data.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.example.courseservice.data.constants.ReactStatus;
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

    private String name;

    @Lob
    private byte[] file;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private VideoStatus status;

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
