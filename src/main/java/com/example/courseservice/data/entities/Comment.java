package com.example.courseservice.data.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.courseservice.data.constants.CommonStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Comment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @SequenceGenerator(name = "comment_sequence", sequenceName = "comment_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "comment_sequence")
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private String studentEmail;

    @Column(columnDefinition = "TEXT")
    private String commented;

    private CommonStatus commonStatus;

    @ManyToOne()
    private Video video;

    @OneToMany(mappedBy = "comment")
    private List<ReplyComment> replyComments;
}
