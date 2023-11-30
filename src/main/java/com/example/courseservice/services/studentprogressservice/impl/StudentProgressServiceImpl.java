package com.example.courseservice.services.studentprogressservice.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.StudentVideoProgress;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.CourseProgressInterface;
import com.example.courseservice.data.object.CourseResponseInterface;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.StudentEnrolledCoursesRepository;
import com.example.courseservice.data.repositories.StudentVideoProgressRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.CourseMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.studentprogressservice.StudentProgressService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class StudentProgressServiceImpl implements StudentProgressService {
    @Autowired
    private StudentVideoProgressRepository studentVideoProgressRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private StudentEnrolledCoursesRepository studentEnrolledCoursesRepository;

    @Override
    public PaginationResponse<List<CourseResponse>> getCourseOfCurrentUser(Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Long studentId = securityContextService.getCurrentUser().getId();
        Page<CourseResponseInterface> courseResponseInterface = studentVideoProgressRepository
                .getCourseDetailsByStudentId(studentId, pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapInterfacesToDtos(courseResponseInterface.getContent()))
                .totalPage(courseResponseInterface.getTotalPages())
                .totalRow(courseResponseInterface.getTotalElements())
                .build();
    }

    @Override
    public void createStudentProgressByVideoId(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new BadRequestException("Cannot found video with Id " + videoId));

        UserInformation currentUser = securityContextService.getCurrentUser();

        if (Boolean.TRUE.equals(
                studentVideoProgressRepository.existsByStudentIdAndVideo(currentUser.getId(), video))) {
            throw new BadRequestException("Video with id " + videoId + " have been finished");
        }

        Course course = video.getCourse();

        studentEnrolledCoursesRepository
                .findByStudentEmailAndCourseId(currentUser.getEmail(), course.getId())
                .orElseThrow(() -> new InValidAuthorizationException("Require Owner permission."));

        studentVideoProgressRepository.save(StudentVideoProgress
                .builder()
                .course(course)
                .studentId(currentUser.getId())
                .video(video)
                .isCompleted(true)
                .completionDate(LocalDateTime.now())
                .build());
    }

    @Override
    public List<CourseResponse> setStudentProgressForListCourseResponse(List<CourseResponse> courseResponses) {
        Long userId = securityContextService.getCurrentUser().getId();
        List<CourseProgressInterface> courseProgressInterfaces = studentVideoProgressRepository
                .getCourseProgressByStudentId(userId);

        if (!courseProgressInterfaces.isEmpty()) {
            Map<Long, Float> courseIdToProgressMap = courseProgressInterfaces.stream()
                    .collect(Collectors.toMap(CourseProgressInterface::getCourseId,
                            CourseProgressInterface::getProgress));
            for (CourseResponse courseResponse : courseResponses) {
                Float progress = courseIdToProgressMap.get(courseResponse.getId());
                if (progress != null) {
                    courseResponse.setProgress(progress);
                    courseResponse.setIsAccess(true);
                }
            }
        }

        return courseResponses;

    }

}
