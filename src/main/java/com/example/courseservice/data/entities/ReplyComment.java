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

import com.example.courseservice.data.constants.CommonStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Reply")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyComment {
    @Id
    @SequenceGenerator(name = "reply_sequence", sequenceName = "reply_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "reply_sequence")
    private long id;

    private LocalDateTime createdDate;

    private LocalDateTime updateTime;

    private String studentEmail;

    private String userAvatar;

    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private CommonStatus commonStatus;

    @ManyToOne()
    private Video video;

    @ManyToOne
    private Comment commentObject;
}
