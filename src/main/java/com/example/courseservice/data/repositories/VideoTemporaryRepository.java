package com.example.courseservice.data.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.VideoTemporary;
import com.example.courseservice.data.object.VideoAdminResponseInterface;

public interface VideoTemporaryRepository extends JpaRepository<VideoTemporary, Long> {
    public Optional<VideoTemporary> findByVideoId(Long videoId);

    public List<VideoTemporary> findByCourseTemporaryIdAndIdIn(Long courseTemporaryId, Set<Long> ids);

    public List<VideoTemporary> findByCourseTemporaryIdAndStatus(Long courseTemporaryId, CommonStatus commonStatus);

    public List<VideoTemporary> findByCourseTemporaryIdAndStatusNot(Long courseTemporaryId, CommonStatus commonStatus);

    public List<VideoTemporary> findByCourseTemporaryId(Long courseTemporaryId);

    @Query("SELECT vt FROM VideoTemporary vt " +
            "WHERE vt.courseTemporary IN " +
            "(SELECT ct FROM CourseTemporary ct " +
            "WHERE ct.teacherId = :teacherId)")
    public Page<VideoTemporary> findVideosByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    public Boolean existsByVideoId(Long videoId);

    @Query("SELECT MAX(v.ordinalNumber) FROM VideoTemporary v WHERE v.courseTemporary = :course")
    public Integer findMaxOrdinalNumberByCourse(@Param("course") CourseTemporary course);

    @Query("SELECT " +
            "vt.id AS id, " +
            "ct.teacherName AS teacherName, " +
            "vt.description AS description, " +
            "ct.subject AS subject, " +
            "ct.name AS courseName, " +
            "vt.urlVideo AS url, "+
            "vt.name AS name, " +
            "vt.duration AS duration, " +
            "vt.urlThumbnail AS thumbnail, "+
            "vt.urlMaterial AS material, "+
            "vt.status AS status, " +
            "vt.createdDate AS createdDate, " +
            "vt.updateTime AS updateDate, " +
            "vt.videoStatus AS videoStatus " +
            "FROM VideoTemporary vt " +
            "JOIN vt.courseTemporary ct " +
            "WHERE vt.id = :videoTemporaryId")
    Optional<VideoAdminResponseInterface> getVideoAdminResponseByVideoTemporaryId(
            @Param("videoTemporaryId") Long videoTemporaryId);
}
