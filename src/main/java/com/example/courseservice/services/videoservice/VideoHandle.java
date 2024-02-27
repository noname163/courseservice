package com.example.courseservice.services.videoservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.VideoStatus;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.StudentVideoProgressRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.VideoMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;

@Component
public class VideoHandle {
    private StudentEnrollCourseService studentEnrollCourseService;
    private StudentVideoProgressRepository studentVideoProgressRepository;
    private VideoRepository videoRepository;
    private VideoMapper videoMapper;
    private SecurityContextService securityContextService;
    private CourseRepository courseRepository;

    public PaginationResponse<List<VideoItemResponse>> getVideoItemResponseForGuest(Course course, Pageable pageable) {
        Page<Video> videos = videoRepository.findByCourseAndStatusOrderByOrdinalNumberAsc(course,
                CommonStatus.AVAILABLE, pageable);
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(setVideoAccessStatus(videoMapper.mapVideosToVideoItemResponses(videos.getContent()),
                        course.getId()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

    public PaginationResponse<List<VideoItemResponse>> getVideoItemResponseForStudent(Course course,
            Pageable pageable) {
        Page<Video> videos = videoRepository.findByCourseAndStatusNotOrderByOrdinalNumberAsc(course,
                CommonStatus.BANNED, pageable);
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(setVideoAccessStatus(videoMapper.mapVideosToVideoItemResponses(videos.getContent()),
                        course.getId()))
                .totalPage(videos.getTotalPages())
                .totalRow(videos.getTotalElements())
                .build();
    }

    public boolean isStudent() {
        return isRole("STUDENT");
    }

    public boolean isTeacher() {
        return isRole("TEACHER");
    }

    public boolean isAdmin() {
        return isRole("ADMIN");
    }

    public boolean isRole(String role) {
        UserInformation userInformation = securityContextService.getCurrentUser();
        return userInformation.getRole().equals(role);
    }

    public Course getCourseForUser(Long courseId) {
        Course course;
        if (securityContextService.isLogin() != null) {
            course = courseRepository.findByIdAndCommonStatusNot(courseId, CommonStatus.BANNED)
                    .orElseThrow(() -> new BadRequestException("Cannot found any course with id " + courseId));
        } else {
            course = courseRepository
                    .findByIdAndCommonStatus(courseId, CommonStatus.AVAILABLE)
                    .orElseThrow(() -> new BadRequestException("Cannot found any course with id " + courseId));
        }
        return course;
    }

    public boolean isVideoAccessible(Video video) {
        boolean isAuthenticatedStudent = securityContextService.getIsAuthenticatedAndIsStudent();
        boolean isVideoPrivate = video.getVideoStatus().equals(VideoStatus.PRIVATE);
        if ((!isAuthenticatedStudent && isVideoPrivate) ||
                (isAuthenticatedStudent && isVideoPrivate && !isStudentEnrolled(video.getCourse().getId()))) {
            return false;
        }

        return true;
    }

    public boolean isStudentEnrolled(Long courseId) {
        String studentEmail = securityContextService.getCurrentUser().getEmail();
        return studentEnrollCourseService.isStudentEnrolled(studentEmail, courseId);
    }

    public List<VideoItemResponse> setVideoAccessStatus(
            List<VideoItemResponse> videoItemResponses, Long courseId) {
        boolean isUserLoggedIn = false;
        if (securityContextService.isLogin() != null && securityContextService.isLogin().getRole().equals("STUDENT")) {
            isUserLoggedIn = true;
        }
        for (VideoItemResponse videoItemResponse : videoItemResponses) {
            boolean isVideoPrivate = videoItemResponse.getVideoStatus().equals(VideoStatus.PRIVATE);
            if (!isUserLoggedIn && isVideoPrivate) {
                videoItemResponse.setIsAccess(false);
            }
            if (!isVideoPrivate) {
                videoItemResponse.setIsAccess(true);
            }

            if (isUserLoggedIn) {
                boolean isEnrolled = isStudentEnrolled(courseId);
                videoItemResponse.setIsAccess(isEnrolled || !isVideoPrivate);
                Set<Long> isWatched = studentVideoProgressRepository
                        .getCompletedVideoIdsByStudentAndCourse(securityContextService.getCurrentUser().getId(),
                                courseId);
                if (!isWatched.isEmpty() && isWatched.contains(videoItemResponse.getId())) {
                    videoItemResponse.setIsWatched(true);
                }
            }
            videoItemResponse.setCourseId(courseId);
        }
        return videoItemResponses;
    }

    public List<VideoItemResponse> setIsAccess(List<VideoItemResponse> videoItemResponses, List<Course> courses) {
        UserInformation currentUser = securityContextService.isLogin();
        List<VideoItemResponse> result = new ArrayList<>();

        if (currentUser != null) {
            List<Long> videoAccess = studentEnrollCourseService.getListVideoIdStudentAccess(currentUser.getEmail(),
                    courses);

            for (VideoItemResponse videoItemResponse : videoItemResponses) {
                if (videoAccess.isEmpty()) {
                    videoItemResponse.setIsAccess(!videoItemResponse.getVideoStatus().equals(VideoStatus.PRIVATE));
                } else {
                    videoItemResponse.setIsAccess(videoAccess.contains(videoItemResponse.getId()));
                }
                result.add(videoItemResponse);
            }
        } else {
            for (VideoItemResponse videoItemResponse : videoItemResponses) {
                videoItemResponse.setIsAccess(videoItemResponse.getVideoStatus().equals(VideoStatus.PUBLIC));
                result.add(videoItemResponse);
            }
        }

        return result;
    }

    public boolean canAccessVideo(Long videoId) {
        return videoRepository.findByIdAndStatusNot(videoId, CommonStatus.BANNED)
                .map(this::isVideoAccessible)
                .orElse(false);
    }

    public boolean isTeacherVideo(Long teacherId, Long videoId) {
        return videoRepository.findByTeacherIdAndVideoId(teacherId, videoId).isPresent();
    }

    public VideoItemResponse getVideoResponse(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new BadRequestException("Not found video with id " + videoId));
        return videoMapper.mapVideoToVideoItemResponse(video);
    }

    public VideoItemResponse getVideoResponseForGuest(Long videoId) {
        Video video = videoRepository.findByIdAndStatus(videoId, CommonStatus.AVAILABLE)
                .orElseThrow(() -> new BadRequestException("Not found video with id " + videoId));
        return videoMapper.mapVideoToVideoItemResponse(video);
    }
}
