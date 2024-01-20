package com.example.courseservice.event;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.services.uploadservice.CloudinaryService;
import com.example.courseservice.services.videoservice.VideoService;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@EnableAsync
public class EventHandler implements ApplicationListener<Event> {
    @Autowired
    private CloudinaryService uploadService;
    @Autowired
    private VideoService videoService;

    @Override
    @Async
    public void onApplicationEvent(Event event) {
        Map<String, Object> data = event.getData();
        VideoResponse videoResponse = (VideoResponse) data.get("videoResponse");

        if (videoResponse != null && videoResponse.getThumbnail() != null && videoResponse.getVideo() != null) {
            CloudinaryUrl video = uploadService.uploadMedia(videoResponse.getVideo());
            CloudinaryUrl thumbnial = uploadService.uploadMedia(videoResponse.getThumbnail());
            VideoUpdate videoUpdate = VideoUpdate
                    .builder()
                    .videoId(videoResponse.getVideoId())
                    .videoUrl(video.getUrl())
                    .duration(video.getDuration())
                    .thumbnailUrl(thumbnial.getUrl())
                    .build();
            if (videoResponse.getMaterial() != null) {
                CloudinaryUrl material = uploadService.uploadMaterial(videoResponse.getMaterial());
                videoUpdate.setMaterial(material.getUrl());
            }

            videoService.insertVideoUrl(videoUpdate);

        }
    }
}
