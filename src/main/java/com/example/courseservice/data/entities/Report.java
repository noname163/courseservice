package com.example.courseservice.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.courseservice.data.constants.ReportType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Report")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @SequenceGenerator(name = "reply_sequence", sequenceName = "reply_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "reply_sequence")
    private Long id;

    private ReportType reportType;

    private String message;

    @Column(columnDefinition = "TEXT")
    private String userAvatar;

    private String userName;

    private Long userId;

    private String userRole;

    @Column(columnDefinition = "TEXT")
    private String url;

    private Long objectId;

    private Boolean isProcessed;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;
}
