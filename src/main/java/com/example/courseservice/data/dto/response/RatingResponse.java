package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RatingResponse {
    private String fullName;
    private Integer rate;
    private String content;
}
