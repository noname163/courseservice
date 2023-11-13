package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Video;

@Component
public class VideoMapper {
    public Video mapDtoToEntity(VideoRequest videoRequest) {
        return Video
                .builder()
                .videoStatus(videoRequest.getVideoStatus())
                .ordinalNumber(videoRequest.getOrder())
                .name(videoRequest.getName())
                .description(videoRequest.getDescription())
                .build();
    }

    public VideoDetailResponse mapEntityToDto(Video video) {
        return VideoDetailResponse
                .builder()
                .url(video.getUrlVideo())
                .thumbnail(video.getUrlThumbnail())
                .like(video.getReaction(ReactStatus.LIKE))
                .dislike(video.getReaction(ReactStatus.DISLIKE))
                .name(video.getName())
                .build();
    }

    public VideoItemResponse mapVideoToVideoItemResponse(Video video) {
        return VideoItemResponse
                .builder()
                .id(video.getId())
                .duration(video.getDuration())
                .name(video.getName())
                .build();
    }

    public VideoAdminResponse mapVideoToVideoAdminResponse(Video video) {
        return VideoAdminResponse
                .builder()
                .id(video.getId())
                .thumbnail(video.getUrlThumbnail())
                .courseName(video.getCourse().getName())
                .name(video.getName())
                .createDate(video.getCreateDate())
                .updateDate(video.getUpdateTime())
                .dislike(video.getReaction(ReactStatus.DISLIKE))
                .like(video.getReaction(ReactStatus.LIKE))
                .status(video.getStatus())
                .videoStatus(video.getVideoStatus())
                .build();
    }

    public List<VideoAdminResponse> mapVideosToVideoAdminResponses(List<Video> videos) {
        return videos
                .stream()
                .map(this::mapVideoToVideoAdminResponse)
                .collect(Collectors.toList());
    }

    public List<VideoItemResponse> mapVideosToVideoItemResponses(List<Video> videos) {
        return videos
                .stream()
                .map(this::mapVideoToVideoItemResponse)
                .collect(Collectors.toList());
    }
}
