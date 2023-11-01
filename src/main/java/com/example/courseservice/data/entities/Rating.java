package com.example.courseservice.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Rating")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @SequenceGenerator(name = "rating_sequence", sequenceName = "rating_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "rating_sequence")
    private long id;

    private int rate;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private Long studentId;

    @ManyToOne()
    private Course course;
}
