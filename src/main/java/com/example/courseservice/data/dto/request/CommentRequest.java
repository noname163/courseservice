package com.example.courseservice.data.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequest {
    private String commentContent;
    private String email;
    private long videoId;
}
