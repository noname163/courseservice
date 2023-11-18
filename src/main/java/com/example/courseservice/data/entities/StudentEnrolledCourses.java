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

import com.example.courseservice.data.constants.StudentEnrolledStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_enrolled_courses", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrolledCourses {
    @Id
    @SequenceGenerator(name = "student_enrolled_courses_sequence", sequenceName = "student_enrolled_courses_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "student_enrolled_courses_sequence")
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private StudentEnrolledStatus status;
    
    private String studentEmail;

    @Column(name = "student_id")
    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
