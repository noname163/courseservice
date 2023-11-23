package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.response.CommentResponse;
import com.example.courseservice.data.entities.Comment;

@Component
public class CommentMapper {
    public Comment mapDtoToEntity(CommentRequest commentRequest) {
        return Comment
                .builder()
                .commented(commentRequest.getCommentContent())
                .build();
    }

    public CommentResponse mapEntityToDto(Comment comment){
        return CommentResponse.builder()
        .email(comment.getStudentEmail())
        .avatar(comment.getUserAvatar())
        .useName(comment.getUserName())
        .createDate(comment.getCreateDate().toLocalDate())
        .comment(comment.getCommented())
        .build();
    }

    public List<CommentResponse> mapEntitiesToDtos(List<Comment> comments){
        return comments.stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }
}
