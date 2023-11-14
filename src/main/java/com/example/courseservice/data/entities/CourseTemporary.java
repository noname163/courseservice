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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.courseservice.data.constants.CommonStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Course_Temporary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseTemporary {
    @Id
    @SequenceGenerator(name = "course_temporary_sequence", sequenceName = "course_temporary_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "course_temporary_sequence")
    private Long id;

    @ManyToOne()
    private Course course;

    private String name;

    private String thumbnial;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime updateTime;

    private Double price;

    private Long levelId;

    private CommonStatus status;
    
}
