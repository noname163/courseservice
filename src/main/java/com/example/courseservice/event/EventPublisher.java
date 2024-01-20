package com.example.courseservice.event;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaymentResponse;
import com.example.courseservice.data.dto.response.VideoResponse;

@Component
public class EventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(FileResponse file) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("URI", request.getRequestURI());
        data.put("video", file);
        applicationEventPublisher.publishEvent(new Event(this, data));
    }
    public void publishEvent(VideoResponse videoResponse) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("URI", request.getRequestURI());
        data.put("videoResponse", videoResponse);
        applicationEventPublisher.publishEvent(new Event(this, data));
    }
    public void publishEvent(PaymentResponse paymentResponse) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("URI", request.getRequestURI());
        data.put("paymentResponse", paymentResponse);
        applicationEventPublisher.publishEvent(new Event(this, data));
    }
}
