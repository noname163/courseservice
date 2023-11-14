package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.entities.VideoTemporary;

@Component
public class VideoTemporaryMapper {
    public VideoTemporary mapDtoToEntity(VideoUpdateRequest videoRequest) {
        return VideoTemporary
                .builder()
                .videoStatus(videoRequest.getVideoStatus())
                .ordinalNumber(videoRequest.getOrder())
                .name(videoRequest.getName())
                .videoStatus(videoRequest.getVideoStatus())
                .description(videoRequest.getDescription())
                .build();
    }

    public Video mapVideoTmpToVideo(VideoTemporary videoTemporary) {
        return Video
                .builder()
                .description(videoTemporary.getDescription())
                .duration(videoTemporary.getDuration())
                .urlThumbnail(videoTemporary.getUrlThumbnail())
                .urlVideo(videoTemporary.getUrlVideo())
                .name(videoTemporary.getName())
                .videoStatus(videoTemporary.getVideoStatus())
                .ordinalNumber(videoTemporary.getOrdinalNumber())
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
}
