package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequest {
    @NotBlank
    @NotNull
    @Size(min = 50, message = "Comment content cannot smaller than 50 characters")
    private String commentContent;
    @NotNull
    @Size(min = 1)
    private long videoId;
}
