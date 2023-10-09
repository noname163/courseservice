package com.example.courseservice.data.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @SequenceGenerator(name = "course_sequence", sequenceName = "course_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "course_sequence")
    private long id;

    private String name;

    private Long teacherId;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private Double price;

    private int level;

    @OneToMany(mappedBy = "course")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "course")
    private List<StudentEnrolledCourses> studentEnrolledCourses;

    @OneToMany(mappedBy = "course")
    private List<CourseTopic> courseTopics;

    @OneToMany(mappedBy = "course")
    private List<Material> materials;
    
    @OneToMany(mappedBy = "course")
    private List<Video> videos;

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
