package com.example.courseservice.mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public CommentResponse mapEntityToDto(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .email(comment.getStudentEmail())
                .avatar(comment.getUserAvatar())
                .useName(comment.getUserName())
                .createdDate(Optional.ofNullable(comment.getCreatedDate().toLocalDate()).orElse(null))
                .upDateTime(comment.getUpdateTime())
                .comment(comment.getCommented())
                .build();
    }

    public List<CommentResponse> mapEntitiesToDtos(List<Comment> comments) {
        if(comments.isEmpty()){
            return List.of();
        }
        return comments.stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }
}
