package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RatingResponse {
    private Long id;
    private String fullName;
    private Integer rate;
    private String content;
    private String avatar;
    private LocalDateTime crateDate;
    private LocalDateTime upDateTime;
}
