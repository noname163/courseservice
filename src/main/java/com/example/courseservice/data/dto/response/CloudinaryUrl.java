package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CloudinaryUrl {
    private String url;
    private String publicId;
    private Float duration;
}
