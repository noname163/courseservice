package com.example.courseservice.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.courseservice.data.entities.Report;
import com.example.courseservice.data.object.ReportResponseInterface;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT " +
            "r.id AS reportId, " +
            "r.userName AS userEmail, " +
            "r.userName AS userName, " +
            "r.userAvatar AS userAvatar, " +
            "r.userRole AS userRole, " +
            "v.name AS objectName, " +
            "r.message AS reportContent, " +
            "r.reportType AS reportType, " +
            "r.createDate AS createDate, " +
            "CASE WHEN r.updateTime IS NOT NULL THEN true ELSE false END AS isProcessed " +
            "FROM Report r " +
            "LEFT JOIN Video v ON r.objectId = v.id " +
            "WHERE r.objectId IS NOT NULL")
    Page<ReportResponseInterface> getReportResponsesForVideos(Pageable pageable);

}
