package com.example.courseservice.mappers;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.entities.Comment;

@Component
public class CommentMapper {
    public Comment mapDtoToEntity(CommentRequest commentRequest) {
        return Comment
                .builder()
                .commented(commentRequest.getCommentContent())
                .studentEmail(commentRequest.getEmail())
                .build();
    }
}
