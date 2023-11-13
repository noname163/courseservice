package com.example.courseservice.data.entities;

import java.time.LocalDateTime;

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
@Table(name = "Material")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material {
    @Id
    @SequenceGenerator(name = "material_sequence", sequenceName = "material_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "material_sequence")
    private long id;

    private String url;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private CommonStatus commonStatus;
    
    @ManyToOne()
    private Video video;
}
