package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateCommentRequest {
    private Long id;
    private String commentContent;
}
