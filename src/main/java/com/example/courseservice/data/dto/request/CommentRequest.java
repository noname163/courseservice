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
    @Size(min = 10, message = "Comment content cannot smaller than 10 characters")
    private String commentContent;
    @NotNull
    private long videoId;
}
