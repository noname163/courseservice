package com.example.courseservice.data.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseDetailResponse{
    private CourseResponse courseResponse;
    private int totalStudent;
    private List<CourseVideoResponse> videoResponse;
    private LocalDateTime updateDate;
}
