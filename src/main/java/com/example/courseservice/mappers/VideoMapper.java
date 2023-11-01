package com.example.courseservice.mappers;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.entities.Video;

@Component
public class VideoMapper {
    public Video mapDtoToEntity(VideoRequest videoRequest) {
        return Video
                .builder()
                .ordinalNumber(videoRequest.getOrder())
                .name(videoRequest.getName())
                .description(videoRequest.getDescription())
                .build();
    }
}
