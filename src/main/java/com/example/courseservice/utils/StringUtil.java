package com.example.courseservice.utils;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.object.MediaType;

@Component
public class StringUtil {
    public MediaType convertStringToMediaType(String mediaTypeString) {
        String[] split = mediaTypeString.split("/");
        return MediaType
                .builder()
                .mediaType(split[0])
                .subfix(split[1])
                .build();
    }
}
