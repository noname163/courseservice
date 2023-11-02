package com.example.courseservice.event;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.dto.response.VideoUrls;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.services.videourlservice.VideoUrlService;
import com.example.courseservice.utils.EnvironmentVariable;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@EnableAsync
public class EventHandler implements ApplicationListener<Event> {
    @Autowired
    private EnvironmentVariable environmentVariables;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoUrlService videoUrlService;

    @Override
    @Async
    public void onApplicationEvent(Event event) {
        Map<String, Object> data = event.getData();
        VideoResponse videoResponse = (VideoResponse) data.get("videoResponse");
        if (videoResponse != null) {
            CloudinaryUrl video = uploadService.uploadMedia(videoResponse.getVideo());
            CloudinaryUrl thumbnial = uploadService.uploadMedia(videoResponse.getThumbnail());
            VideoUpdate videoUpdate = VideoUpdate
                    .builder()
                    .videoId(videoResponse.getVideoId())
                    .videoUrl(video.getUrl())
                    .thumbnailUrl(thumbnial.getUrl())
                    .build();
            videoService.insertVideoUrl(videoUpdate);
            List<VideoUrls> videoUrls = uploadService.splitVideo(video.getPublicId(), environmentVariables.getVideoMaxSegment(), video.getDuration());
            videoUrlService.insertVideoUrl(videoUrls, videoResponse.getVideoId());
        }
    }
}
