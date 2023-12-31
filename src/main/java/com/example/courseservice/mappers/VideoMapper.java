package com.example.courseservice.mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.constants.VideoStatus;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.CourseVideoResponseInterface;
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

    public VideoDetailResponse mapEntityToDto(Video video) {
        return VideoDetailResponse
                .builder()
                .videoStatus(video.getVideoStatus())
                .url(video.getUrlVideo())
                .description(video.getDescription())
                .thumbnail(video.getUrlThumbnail())
                .material(video.getUrlMaterial())
                .like(video.getReaction(ReactStatus.LIKE))
                .dislike(video.getReaction(ReactStatus.DISLIKE))
                .name(video.getName())
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
                .id(video.getId())
                .description(video.getDescription())
                .thumbnail(video.getUrlThumbnail())
                .courseName(video.getCourse().getName())
                .name(video.getName())
                .teacherName(video.getCourse().getTeacherName())
                .subject(video.getCourse().getSubject())
                .duration(video.getDuration())
                .createdDate(video.getCreatedDate())
                .updateDate(video.getUpdateTime())
                .dislike(video.getReaction(ReactStatus.DISLIKE))
                .like(video.getReaction(ReactStatus.LIKE))
                .status(video.getStatus())
                .videoStatus(video.getVideoStatus())
                .ordinalNumber(video.getOrdinalNumber())
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

    public CourseVideoResponse mapToCourseVideoResponse(CourseVideoResponseInterface courseVideoResponseInterface) {
        if (courseVideoResponseInterface == null) {
            return null;
        }

        return CourseVideoResponse.builder()
                .id(courseVideoResponseInterface.getId())
                .name(courseVideoResponseInterface.getName())
                .thumbnail(courseVideoResponseInterface.getThumbnail())
                .duration(courseVideoResponseInterface.getDuration())
                .totalLike(courseVideoResponseInterface.getTotalLike())
                .totalComment(courseVideoResponseInterface.getTotalComment())
                .videoStatus(courseVideoResponseInterface.getVideoStatus())
                .isWatched(false)
                .isDraft(false)
                .url(courseVideoResponseInterface.getVideoStatus().equals(VideoStatus.PUBLIC)
                        ? courseVideoResponseInterface.getUrl()
                        : "")
                .ordinalNumber(Optional.ofNullable(courseVideoResponseInterface.getOrdinalNumber()).orElse(0))
                .build();
    }

    public List<CourseVideoResponse> mapToCourseVideoResponseList(
            List<CourseVideoResponseInterface> courseVideoResponseInterfaces) {
        return courseVideoResponseInterfaces.stream()
                .map(this::mapToCourseVideoResponse)
                .collect(Collectors.toList());
    }

    public VideoAdminResponse mapToVideoAdminResponse(VideoAdminResponseInterface videoAdminResponseInterface) {
        if (videoAdminResponseInterface == null) {
            return null;
        }

        return VideoAdminResponse.builder()
                .id(videoAdminResponseInterface.getId())
                .url(videoAdminResponseInterface.getUrl())
                .name(videoAdminResponseInterface.getName())
                .teacherName(videoAdminResponseInterface.getTeacherName())
                .teacherAvatar(videoAdminResponseInterface.getTeacherAvatar())
                .description(videoAdminResponseInterface.getDescription())
                .subject(videoAdminResponseInterface.getSubject())
                .thumbnail(videoAdminResponseInterface.getThumbnail())
                .material(videoAdminResponseInterface.getMaterial())
                .courseName(videoAdminResponseInterface.getCourseName())
                .like(Optional.ofNullable(videoAdminResponseInterface.getLike()).orElse(0l))
                .dislike(Optional.ofNullable(videoAdminResponseInterface.getDislike()).orElse(0l))
                .duration(videoAdminResponseInterface.getDuration())
                .status(videoAdminResponseInterface.getStatus())
                .createdDate(videoAdminResponseInterface.getCreatedDate())
                .updateDate(videoAdminResponseInterface.getUpdateDate())
                .videoStatus(videoAdminResponseInterface.getVideoStatus())
                .ordinalNumber(videoAdminResponseInterface.getOrdinalNumber())
                .build();
    }

    public List<VideoAdminResponse> mapToVideoAdminResponseList(List<VideoAdminResponseInterface> videoAdminResponseInterfaces) {
        if(videoAdminResponseInterfaces.isEmpty()){
            return List.of();
        }
        return videoAdminResponseInterfaces.stream()
                .map(this::mapToVideoAdminResponse)
                .collect(Collectors.toList());
    }
}
