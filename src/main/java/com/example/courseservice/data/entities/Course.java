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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Entity
@Table(name = "Course")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @SequenceGenerator(name = "course_sequence", sequenceName = "course_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "course_sequence")
    private Long id;

    private String name;

    private String teacherEmail;

    private Long teacherId;

    private String teacherName;

    private String teacherAvatar;

    private String thumbnial;

    private String cloudinaryId;

    private Long subjectId;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Default
    private LocalDateTime createdDate = LocalDateTime.now();

    @Default
    private LocalDateTime updateTime= LocalDateTime.now();

    private Double price;

    @Enumerated(EnumType.STRING)
    private CommonStatus commonStatus;

    @OneToOne()
    @JoinColumn(name = "course_id")
    private Course courseId;

    @ManyToOne()
    @JoinColumn(name = "level_id")
    private Level level;

    @OneToMany(mappedBy = "course")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "course")
    private List<StudentEnrolledCourses> studentEnrolledCourses;

    @OneToMany(mappedBy = "course")
    private List<CourseTopic> courseTopics;

    @OneToMany(mappedBy = "course")
    private List<Transaction> transactions;
    
    @OneToMany(mappedBy = "course")
    private List<Video> videos;

    @OneToMany(mappedBy = "course")
    private List<StudentVideoProgress> studentVideoProgresses;

    @OneToMany(mappedBy = "course")
    private List<TeacherIncome> teacherIncomes;

    @Transient 
    public float getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0f;
        }

        int sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getRate();
        }

        return (float) sum / ratings.size();
    }
}
