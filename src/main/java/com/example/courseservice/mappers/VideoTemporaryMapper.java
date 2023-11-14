package com.example.courseservice.mappers;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.request.VideoUpdateRequest;
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
}
