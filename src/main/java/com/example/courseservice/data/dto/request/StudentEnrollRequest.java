package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StudentEnrollRequest {
    private Long courseId;
    private Long studentId;
    private String email;
}
