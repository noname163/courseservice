package com.example.courseservice.mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.entities.VideoTemporary;
import com.example.courseservice.data.object.VideoAdminResponseInterface;

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

    public Video mapVideoTmpToReal(VideoTemporary videoTemporary, Course course) {
        return Video
                .builder()
                .course(course)
                .name(videoTemporary.getName())
                .description(videoTemporary.getDescription())
                .duration(videoTemporary.getDuration())
                .ordinalNumber(videoTemporary.getOrdinalNumber())
                .urlThumbnail(videoTemporary.getUrlThumbnail())
                .urlMaterial(videoTemporary.getUrlMaterial())
                .urlVideo(videoTemporary.getUrlVideo())
                .videoStatus(videoTemporary.getVideoStatus())
                .createDate(videoTemporary.getCreateDate())
                .updateTime(videoTemporary.getUpdateTime())
                .status(CommonStatus.AVAILABLE)
                .build();
    }

    public List<Video> mapVideosTmpToReal(List<VideoTemporary> videoTemporaries, Course course) {
        return videoTemporaries
                .stream()
                .map(videoTemporary -> mapVideoTmpToReal(videoTemporary, course))
                .collect(Collectors.toList());
    }

    public VideoItemResponse mapVideoItemResponse(VideoTemporary videoTemporary) {
        return VideoItemResponse
                .builder()
                .id(videoTemporary.getId())
                .name(videoTemporary.getName())
                .material(videoTemporary.getUrlMaterial())
                .duration(videoTemporary.getDuration())
                .thumbnial(videoTemporary.getUrlThumbnail())
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

    public CourseVideoResponse mapToCourseVideoResponse(VideoTemporary videoTemporary) {
        return CourseVideoResponse
                .builder()
                .id(videoTemporary.getId())
                .name(videoTemporary.getName())
                .duration(videoTemporary.getDuration())
                .thumbnail(videoTemporary.getUrlThumbnail())
                .ordinalNumber(0)
                .duration(videoTemporary.getDuration())
                .build();
    }

    public List<CourseVideoResponse> mapToCourseVideosResponse(List<VideoTemporary> videoTemporaries) {
        return videoTemporaries
                .stream()
                .map(this::mapToCourseVideoResponse)
                .collect(Collectors.toList());
    }

    public VideoAdminResponse mapToVideoAdminResponse(VideoAdminResponseInterface videoAdminResponseInterface) {
        if (videoAdminResponseInterface == null) {
            return null;
        }

        return VideoAdminResponse.builder()
                .id(videoAdminResponseInterface.getId())
                .name(videoAdminResponseInterface.getName())
                .teacherName(videoAdminResponseInterface.getTeacherName())
                .description(videoAdminResponseInterface.getDescription())
                .subject(videoAdminResponseInterface.getSubject())
                .thumbnail(videoAdminResponseInterface.getThumbnail())
                .courseName(videoAdminResponseInterface.getCourseName())
                .duration(videoAdminResponseInterface.getDuration())
                .status(videoAdminResponseInterface.getStatus())
                .createDate(videoAdminResponseInterface.getCreateDate())
                .updateDate(videoAdminResponseInterface.getUpdateDate())
                .videoStatus(videoAdminResponseInterface.getVideoStatus())
                .build();
    }

    public List<VideoAdminResponse> mapToVideoAdminResponseList(List<VideoAdminResponseInterface> videoAdminResponseInterfaces) {
        return videoAdminResponseInterfaces.stream()
                .map(this::mapToVideoAdminResponse)
                .collect(Collectors.toList());
    }
}
