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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Course_Topic",uniqueConstraints = @UniqueConstraint(columnNames = {"topic_id", "course_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseTopic {
    @Id
    @SequenceGenerator(name = "course_topic_sequence", sequenceName = "course_topic_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "course_topic_sequence")
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;
    
    @Column(name = "topic_id")
    private Long topicId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
