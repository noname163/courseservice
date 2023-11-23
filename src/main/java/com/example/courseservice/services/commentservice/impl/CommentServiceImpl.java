package com.example.courseservice.services.commentservice.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.request.UpdateCommentRequest;
import com.example.courseservice.data.dto.response.CommentResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Comment;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CommentRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.CommentMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.commentservice.CommentService;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.services.videoservice.VideoService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentEnrollCourseService studentEnrollCourseService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private VideoService videoService;

    @Override
    public void createComment(CommentRequest commentRequest) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        String email = currentUser.getEmail();
        Video video = videoService.getVideoByIdAndCommonStatus(commentRequest.getVideoId(), CommonStatus.AVAILABLE);
        long courseId = video.getCourse().getId();
        if (courseService.isCourseBelongTo(email, courseId)
                || studentEnrollCourseService.isStudentEnrolled(email, courseId)) {
            Comment comment = Comment.builder()
                    .createDate(LocalDateTime.now())
                    .userAvatar(currentUser.getAvatar())
                    .studentEmail(email)
                    .studentId(currentUser.getId())
                    .userName(currentUser.getFullname())
                    .commented(commentRequest.getCommentContent())
                    .commonStatus(CommonStatus.AVAILABLE)
                    .video(video)
                    .build();
            commentRepository.save(comment);
        }
        else{
            throw new BadRequestException("Need owner permission to comment");
        }

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
    public PaginationResponse<List<CommentResponse>> getListCommentByVideoId(long videoId, Integer page, Integer size,
            String field, SortType sortType) {

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<Comment> comments = commentRepository.findByVideoIdAndCommonStatus(videoId, CommonStatus.AVAILABLE,
                pageable);

        return PaginationResponse.<List<CommentResponse>>builder()
                .data(commentMapper.mapEntitiesToDtos(comments.getContent()))
                .totalPage(comments.getTotalPages())
                .totalRow(comments.getTotalElements())
                .build();

    }

    @Override
    public void deleteComments(Video video) {
        commentRepository.deleteByVideo(video);
    }

}
