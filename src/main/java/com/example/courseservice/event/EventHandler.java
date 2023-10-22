package com.example.courseservice.event;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.services.uploadservice.UploadService;
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

    @Override
    @Async
    public void onApplicationEvent(Event event) {
        Map<String, Object> data = (Map<String, Object>) event.getData();
        FileResponse file = (FileResponse) data.get("video");
        if (file != null) {
            uploadService.uploadMedia(file);
        }
    }
}
