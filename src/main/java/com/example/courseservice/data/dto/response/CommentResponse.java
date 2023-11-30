package com.example.courseservice.data.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CommentResponse {
    private Long id;
    private String email;
    private String useName;
    private LocalDate createdDate;
    private LocalDateTime upDateTime;
    private String comment;
    private String avatar;
}
