package com.example.courseservice.data.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CommentResponse {
    private String fullName;
    private LocalDate createDate;
    private String comment;
}
