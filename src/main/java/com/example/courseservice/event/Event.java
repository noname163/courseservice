package com.example.courseservice.event;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class Event extends ApplicationEvent {

    private Map<String,Object> data;

    public Event(Object source, Map<String,Object> data) {
        super(source);
        this.data = data;
    }
    
}
