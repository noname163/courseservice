package com.example.courseservice.services.reactvideoservice.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.dto.request.ReactRequest;
import com.example.courseservice.data.entities.ReactVideo;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.ReactVideoRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.reactvideoservice.ReactVideoService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.services.videoservice.VideoService;

@Service
public class ReactVideoServiceImpl implements ReactVideoService {

    @Autowired
    private ReactVideoRepository reactVideoRepository;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private StudentEnrollCourseService studentEnrollCourseService;
    @Autowired
    private VideoRepository videoRepository;

    @Override
    public void createReact(ReactRequest reactRequest) {

        UserInformation currentUser = securityContextService.getCurrentUser();
        Video video = videoRepository
                .findByIdAndStatus(reactRequest.getVideoId(), CommonStatus.AVAILABLE)
                .orElseThrow(() -> new BadRequestException("Not exist video with id " + reactRequest.getVideoId()));

        if (studentEnrollCourseService.isStudentEnrolled(currentUser.getEmail(), video.getCourse().getId())) {
            Optional<ReactVideo> reactVideoOtp = reactVideoRepository
                    .findByVideoIdAndStudentId(reactRequest.getVideoId(), currentUser.getId());
            if (reactVideoOtp.isEmpty()) {
                reactVideoRepository.save(ReactVideo
                        .builder()
                        .reactStatus(reactRequest.getStatus())
                        .createdDate(LocalDateTime.now())
                        .video(video)
                        .studentId(currentUser.getId())
                        .studentName(currentUser.getFullname())
                        .build());
            } else {
                ReactVideo reactVideo = reactVideoOtp.get();
                if (reactVideo.getReactStatus().equals(reactRequest.getStatus())) {
                    reactVideo.setReactStatus(ReactStatus.NONE);
                } else {
                    reactVideo.setUpdateTime(LocalDateTime.now());
                    reactVideo.setReactStatus(reactRequest.getStatus());
                }
                reactVideoRepository.save(reactVideo);
            }

        }
    }

    @Override
    public ReactStatus getReactStatusByStudentIdAndVideoId(Long videoId) {
        UserInformation currentUser = securityContextService.isLogin();
        if (currentUser == null) {
            return ReactStatus.NONE;
        }
        Optional<ReactVideo> reactVideo = reactVideoRepository.findByVideoIdAndStudentId(videoId, currentUser.getId());
        if (reactVideo.isEmpty()) {
            return ReactStatus.NONE;
        }
        return reactVideo.get().getReactStatus();
    }
}
