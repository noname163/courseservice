package com.example.courseservice.mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.constants.VideoStatus;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.VideoAdminResponseInterface;
import com.example.courseservice.data.object.VideoItemResponseInterface;

@Component
public class VideoMapper {
    public VideoItemResponse mapToVideoItemResponse(VideoItemResponseInterface videoItemResponseInterface) {
        if (videoItemResponseInterface == null) {
            return null;
        }

        return VideoItemResponse.builder()
                .id(videoItemResponseInterface.getId())
                .name(videoItemResponseInterface.getName())
                .duration(videoItemResponseInterface.getDuration())
                .videoStatus(videoItemResponseInterface.getVideoStatus())
                .isAccess(videoItemResponseInterface.getIsAccess())
                .ordinalNumber(videoItemResponseInterface.getOrdinalNumber())
                .build();
    }

    public List<VideoItemResponse> mapToVideoItemResponse(
            List<VideoItemResponseInterface> videoItemResponseInterfaces) {
        return videoItemResponseInterfaces.stream().map(this::mapToVideoItemResponse).collect(Collectors.toList());
    }

    public Video mapDtoToEntity(VideoRequest videoRequest) {
        return Video
                .builder()
                .videoStatus(videoRequest.getVideoStatus())
                .ordinalNumber(videoRequest.getOrder())
                .name(videoRequest.getName())
                .description(videoRequest.getDescription())
                .build();
    }

    public VideoItemResponse mapVideoToVideoItemResponse(Video video) {
        return VideoItemResponse
                .builder()
                .id(video.getId())
                .videoStatus(video.getVideoStatus())
                .duration(video.getDuration())
                .thumbnail(video.getUrlThumbnail())
                .videoUrl(video.getVideoStatus().equals(VideoStatus.PUBLIC) ? video.getUrlVideo() : null)
                .createdDate(video.getCreatedDate())
                .videoUrl(video.getVideoStatus().equals(VideoStatus.PUBLIC) ? video.getUrlVideo() : "")
                .like(video.getReaction(ReactStatus.LIKE))
                .material(video.getUrlMaterial())
                .name(video.getName())
                .isWatched(false)
                .ordinalNumber(video.getOrdinalNumber())
                .build();
    }

    public VideoAdminResponse mapVideoToVideoAdminResponse(Video video) {
        return VideoAdminResponse
                .builder()
                .courseName(video.getCourse().getName())
                .subject(video.getCourse().getSubject())
                .dislike(video.getReaction(ReactStatus.DISLIKE))
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

    public VideoItemResponse mapVideoItemInterfaceToReal(VideoItemResponseInterface videoItemResponseInterface) {
        return VideoItemResponse
                .builder()
                .duration(videoItemResponseInterface.getDuration())
                .id(videoItemResponseInterface.getId())
                .isAccess(videoItemResponseInterface.getIsAccess())
                .videoStatus(videoItemResponseInterface.getVideoStatus())
                .name(videoItemResponseInterface.getName())
                .ordinalNumber(videoItemResponseInterface.getOrdinalNumber())
                .createdDate(videoItemResponseInterface.getcreatedDate())
                .build();
    }

    public List<VideoItemResponse> mapVideoItemInterfaceToReal(
            List<VideoItemResponseInterface> videoItemResponseInterfaces) {
        return videoItemResponseInterfaces.stream().map(this::mapVideoItemInterfaceToReal).collect(Collectors.toList());
    }

    public VideoItemResponse mapVideoAdminResponseToVideoItem(VideoAdminResponseInterface videoAdminResponseInterface) {
        return VideoItemResponse
                .builder()
                .duration(videoAdminResponseInterface.getDuration())
                .description(videoAdminResponseInterface.getDescription())
                .id(videoAdminResponseInterface.getId())
                .videoStatus(videoAdminResponseInterface.getVideoStatus())
                .videoUrl(videoAdminResponseInterface.getUrl())
                .name(videoAdminResponseInterface.getName())
                .material(videoAdminResponseInterface.getMaterial())
                .thumbnail(videoAdminResponseInterface.getThumbnail())
                .ordinalNumber(videoAdminResponseInterface.getOrdinalNumber())
                .createdDate(videoAdminResponseInterface.getCreatedDate())
                .videoAdminResponse(mapToVideoAdminResponse(videoAdminResponseInterface))
                .build();
    }

    public List<VideoItemResponse> mapVideosAdminResponseToVideosItem(
            List<VideoAdminResponseInterface> videoAdminResponseInterfaces) {
        if (videoAdminResponseInterfaces == null) {
            return List.of();
        }

        return videoAdminResponseInterfaces
                .stream()
                .map(this::mapVideoAdminResponseToVideoItem)
                .collect(Collectors.toList());
    }

    public VideoAdminResponse mapToVideoAdminResponse(VideoAdminResponseInterface videoAdminResponseInterface) {
        if (videoAdminResponseInterface == null) {
            return null;
        }

        return VideoAdminResponse.builder()
                .subject(videoAdminResponseInterface.getSubject())
                .teacherAvatar(videoAdminResponseInterface.getTeacherAvatar())
                .teacherName(videoAdminResponseInterface.getTeacherName())
                .courseName(videoAdminResponseInterface.getCourseName())
                .dislike(Optional.ofNullable(videoAdminResponseInterface.getDislike()).orElse(0l))
                .status(videoAdminResponseInterface.getStatus())
                .videoStatus(videoAdminResponseInterface.getVideoStatus())
                .build();
    }

    public List<VideoAdminResponse> mapToVideoAdminResponseList(
            List<VideoAdminResponseInterface> videoAdminResponseInterfaces) {
        if (videoAdminResponseInterfaces.isEmpty()) {
            return List.of();
        }
        return videoAdminResponseInterfaces.stream()
                .map(this::mapToVideoAdminResponse)
                .collect(Collectors.toList());
    }
}
