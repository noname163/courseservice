package com.example.courseservice.data.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Level")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Level {
    @Id
    @SequenceGenerator(name = "level_sequence", sequenceName = "level_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "levl_sequence")
    private long id;

    private String name;

    @OneToMany(mappedBy = "level")
    private List<Course> courses;
}
