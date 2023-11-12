package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.entities.Video;

@Component
public class VideoCourseMapper {
    public CourseVideoResponse mapEntityToDto(Video video) {
        return CourseVideoResponse
                .builder()
                .id(video.getId())
                .name(video.getName())
                .duration(video.getDuration())
                .totalComment(video.getComments().size())
                .totalLike(video.getReaction(ReactStatus.LIKE))
                .build();
    }

    public List<CourseVideoResponse> mapEntitiesToDtos(List<Video> videos) {
        return videos
                .stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }
}
