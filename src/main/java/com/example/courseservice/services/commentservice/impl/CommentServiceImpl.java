package com.example.courseservice.services.commentservice.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.request.UpdateCommentRequest;
import com.example.courseservice.data.dto.response.CommentResponse;
import com.example.courseservice.data.entities.Comment;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CommentRepository;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.commentservice.CommentService;
import com.example.courseservice.services.videoservice.VideoService;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private VideoService videoService;

    @Override
    public void createComment(CommentRequest commentRequest) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        String email = currentUser.getEmail();

        Video video = videoService.getVideoById(commentRequest.getVideoId());

        Comment comment = Comment.builder()
                .createDate(LocalDateTime.now())
                .studentEmail(email)
                .commented(commentRequest.getCommentContent())
                .commonStatus(CommonStatus.AVAILABLE)
                .video(video)
                .build();

        commentRepository.save(comment);
    }

    @Override
    public void editContent(UpdateCommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentRequest.getId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Comment not found with id: " + commentRequest.getId()));
        UserInformation currentUser = securityContextService.getCurrentUser();
        if (!comment.getStudentEmail().equals(currentUser.getEmail())) {
            throw new InValidAuthorizationException("User not allow to edit this comment");
        }
        comment.setCommented(commentRequest.getCommentContent());
        comment.setUpdateTime(LocalDateTime.now());

        commentRepository.save(comment);
    }

    @Override
    public List<CommentResponse> getListCommentByVideoId(long videoId) {
        List<Comment> comments = commentRepository.findByVideoId(videoId);
        return comments.stream()
                .map(comment -> CommentResponse.builder()
                        .email(comment.getStudentEmail())
                        .createDate(comment.getCreateDate().toLocalDate())
                        .comment(comment.getCommented())
                        .build())
                .collect(Collectors.toList());

    }

}
