package com.example.courseservice.mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.entities.VideoTemporary;

@Component
public class VideoTemporaryMapper {
    public VideoTemporary mapDtoToEntity(VideoUpdateRequest videoRequest, Video video) {
        return VideoTemporary
                .builder()
                .videoStatus(Optional.ofNullable(videoRequest.getVideoStatus()).orElse(video.getVideoStatus()))
                .name(Optional.ofNullable(videoRequest.getName()).orElse(video.getName()))
                .description(Optional.ofNullable(videoRequest.getDescription()).orElse(video.getDescription()))
                .build();
    }

    public Video mapVideoTmpToVideo(Video video, VideoTemporary videoTemporary) {
        video.setDescription(videoTemporary.getDescription());
        video.setDuration(videoTemporary.getDuration());
        video.setName(videoTemporary.getName());
        video.setUpdateTime(videoTemporary.getUpdateTime());
        video.setUrlThumbnail(videoTemporary.getUrlThumbnail());
        video.setUrlVideo(videoTemporary.getUrlVideo());
        video.setVideoStatus(videoTemporary.getVideoStatus());
        video.setOrdinalNumber(videoTemporary.getOrdinalNumber());
        return video;
    }

    public VideoTemporary mapDtoToEntity(VideoRequest videoRequest) {
        return VideoTemporary
                .builder()
                .videoStatus(videoRequest.getVideoStatus())
                .ordinalNumber(videoRequest.getOrder())
                .name(videoRequest.getName())
                .description(videoRequest.getDescription())
                .build();
    }

    public VideoAdminResponse mapVideoToVideoAdminResponse(VideoTemporary video) {
        return VideoAdminResponse
                .builder()
                .id(video.getId())
                .thumbnail(video.getUrlThumbnail())
                .courseName(video.getCourse().getName())
                .name(video.getName())
                .teacherName(video.getCourse().getTeacherName())
                .subject(video.getCourse().getSubject())
                .duration(video.getDuration())
                .updateDate(video.getUpdateTime())
                .status(video.getStatus())
                .videoStatus(video.getVideoStatus())
                .build();
    }

    public List<VideoAdminResponse> mapVideosToVideoAdminResponses(List<VideoTemporary> videos) {
        return videos
                .stream()
                .map(this::mapVideoToVideoAdminResponse)
                .collect(Collectors.toList());
    }

    public Video mapVideoTmpToReal(VideoTemporary videoTemporary) {
        return Video
                .builder()
                .name(videoTemporary.getName())
                .description(videoTemporary.getDescription())
                .duration(videoTemporary.getDuration())
                .ordinalNumber(videoTemporary.getOrdinalNumber())
                .urlThumbnail(videoTemporary.getUrlThumbnail())
                .urlVideo(videoTemporary.getUrlVideo())
                .videoStatus(videoTemporary.getVideoStatus())
                .createDate(videoTemporary.getCreateDate())
                .updateTime(videoTemporary.getUpdateTime())
                .status(CommonStatus.AVAILABLE)
                .build();
    }

    public List<Video> mapVideosTmpToReal(List<VideoTemporary> videoTemporaries) {
        return videoTemporaries
                .stream()
                .map(this::mapVideoTmpToReal)
                .collect(Collectors.toList());
    }

    public VideoItemResponse mapVideoItemResponse(VideoTemporary videoTemporary) {
        return VideoItemResponse
                .builder()
                .name(videoTemporary.getName())
                .duration(videoTemporary.getDuration())
                .thumnial(videoTemporary.getUrlThumbnail())
                .videoUrl(videoTemporary.getUrlVideo())
                .videoStatus(videoTemporary.getVideoStatus())
                .build();
    }

    public List<VideoItemResponse> mapVideoItemResponses(List<VideoTemporary> videoTemporaries) {
        return videoTemporaries
                .stream()
                .map(this::mapVideoItemResponse)
                .collect(Collectors.toList());
    }
}
