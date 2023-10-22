package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CloudinaryUrl {
    String url;
    Float duration;
}
