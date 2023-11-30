package com.example.courseservice.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.ReportType;
import com.example.courseservice.data.entities.Report;
import com.example.courseservice.data.object.ReportResponseInterface;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT " +
            "r.id AS reportId, " +
            "r.userName AS userName, " +
            "r.userAvatar AS userAvatar, " +
            "r.userRole AS userRole, " +
            "v.name AS objectName, " +
            "r.message AS reportContent, " +
            "r.reportType AS reportType, " +
            "r.createdDate AS createdDate, " +
            "r.isProcessed AS isProcessed, "+
            "r.objectId AS objectId "+
            "FROM Report r " +
            "LEFT JOIN Video v ON r.objectId = v.id " +
            "WHERE r.objectId IS NOT NULL AND r.isProcessed = false")
    Page<ReportResponseInterface> getReportResponsesForVideos(Pageable pageable);

    @Query("SELECT " +
            "r.id AS reportId, " +
            "r.userName AS userName, " +
            "r.userAvatar AS userAvatar, " +
            "r.userRole AS userRole, " +
            "v.name AS objectName, " +
            "r.message AS reportContent, " +
            "r.reportType AS reportType, " +
            "r.createdDate AS createdDate, " +
            "r.isProcessed AS isProcessed, "+
            "r.objectId AS objectId "+
            "FROM Report r " +
            "LEFT JOIN Video v ON r.objectId = v.id " +
            "WHERE r.objectId IS NOT NULL "+
            "AND r.isProcessed = false " +
            " AND r.reportType = :status")
    Page<ReportResponseInterface> getReportResponsesForVideosByStatus(@Param("status")ReportType reportType,Pageable pageable);

}
