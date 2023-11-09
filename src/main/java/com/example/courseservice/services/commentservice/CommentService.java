package com.example.courseservice.services.commentservice;

import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.response.CommentResponse;

public interface CommentService {
    public void createComment(CommentRequest commentRequest);
    public void editContent(CommentRequest commentRequest);
    public CommentResponse getListCommentByVideoId(long videoId);
}
