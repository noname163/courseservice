package com.example.courseservice.data.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentNoteResponse {
    private Long id;
    private String note;
    private String duration;
    private LocalDateTime createdDate;
}
