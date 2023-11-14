package com.example.courseservice.services.videotmpservice.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.entities.VideoTemporary;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.data.repositories.VideoTemporaryRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.VideoTemporaryMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.services.videotmpservice.VideoTmpService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class VideoTmpServiceImpl implements VideoTmpService {

    @Autowired
    private VideoTemporaryRepository videoTemporaryRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideoTemporaryMapper videoTemporaryMapper;
    @Autowired
    private VideoService videoService;
    @Autowired
    private FileService fileService;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private PageableUtil pageableUtil;

    @Override
    public VideoResponse saveTempVideo(VideoUpdateRequest videoUpdateRequest, MultipartFile video,
            MultipartFile thumbnail) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Video videoOld = videoService.getVideoById(videoUpdateRequest.getVideoId());

        if (!videoOld.getCourse().getTeacherEmail().equals(currentUser.getEmail())) {
            throw new InValidAuthorizationException("Cannot edit this video");
        }

        VideoTemporary videoConvert = videoTemporaryMapper.mapDtoToEntity(videoUpdateRequest);

        videoConvert.setStatus(CommonStatus.WAITING);
        videoConvert.setCourse(videoOld.getCourse());
        VideoTemporary videoInsert = videoTemporaryRepository.save(videoConvert);
        FileResponse videoFile = fileService.fileStorage(video);
        FileResponse thumbnialFile = fileService.fileStorage(thumbnail);

        return VideoResponse
                .builder()
                .videoId(videoInsert.getId())
                .video(videoFile)
                .thumbnail(thumbnialFile)
                .build();
    }

    @Override
    public void insertVideoUrl(VideoUpdate videoUpdate) {
        if (videoUpdate.getThumbnailUrl() == null
                || videoUpdate.getThumbnailUrl().equals("")
                || videoUpdate.getVideoId() < 0
                || videoUpdate.getVideoId() == null
                || videoUpdate.getVideoUrl().equals("")
                || videoUpdate.getVideoUrl() == null) {
            throw new BadRequestException("Data not valid");
        }
        VideoTemporary video = videoTemporaryRepository
                .findById(videoUpdate.getVideoId())
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found Video temporary with id " + videoUpdate.getVideoId()));
        video.setUrlVideo(videoUpdate.getVideoUrl());
        video.setUrlThumbnail(videoUpdate.getThumbnailUrl());
        video.setStatus(CommonStatus.UPDATING);
        videoTemporaryRepository.save(video);
    }

    @Override
    public void insertVideoTmpToReal(Long videoId) {
        Video video = videoService.getVideoById(videoId);
        VideoTemporary videoTemporary = videoTemporaryRepository
                .findByVideoId(videoId)
                .orElseThrow(() -> new BadRequestException("Cannot found update information of video id " + videoId));
        video = videoTemporaryMapper.mapVideoTmpToVideo(video, videoTemporary);
        videoRepository.save(video);
        videoTemporaryRepository.delete(videoTemporary);
    }

    @Override
    public PaginationResponse<List<VideoAdminResponse>> getUpdateVideo(Integer page, Integer size, String field,
            SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<VideoTemporary> videotemp = videoTemporaryRepository.findAll(pageable);
        return PaginationResponse.<List<VideoAdminResponse>>builder()
                .data(videoTemporaryMapper.mapVideosToVideoAdminResponses(videotemp.getContent()))
                .totalPage(videotemp.getTotalPages())
                .totalRow(videotemp.getTotalElements())
                .build();
    }

    @Override
    public boolean isUpdate(Long videoId) {
        Optional<VideoTemporary> videoTemporary = videoTemporaryRepository
                .findByVideoId(videoId);
        if (videoTemporary.isEmpty()) {
            return false;
        }
        return true;
    }

}
