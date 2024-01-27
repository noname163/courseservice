package com.example.courseservice.event;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.FileConvertResponse;
import com.example.courseservice.data.entities.Video;
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
        FileConvertResponse videoResponse = (FileConvertResponse) data.get("videoResponse");
        Video videoObj = (Video) data.get("video");
        CloudinaryUrl videoCloudinaryUrl = null;
        CloudinaryUrl thumbnialCloudinaryUrl = null;
        CloudinaryUrl materialCloudinaryUrl = null;

        if (videoResponse.getMaterial() != null) {
            materialCloudinaryUrl = uploadService.uploadMaterial(videoResponse.getMaterial());
        }
        if (videoResponse.getVideo() != null) {
            videoCloudinaryUrl = uploadService.uploadMedia(videoResponse.getVideo());
        }
        if (videoResponse.getThumbnail() != null) {
            thumbnialCloudinaryUrl = uploadService.uploadMedia(videoResponse.getThumbnail());
        }
        videoService.saveVideoFile(videoObj, videoCloudinaryUrl, materialCloudinaryUrl, thumbnialCloudinaryUrl);
    }
}
