package com.example.courseservice.services.commentservice;

import java.util.List;

import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.request.UpdateCommentRequest;
import com.example.courseservice.data.dto.response.CommentResponse;

public interface CommentService {
    public void createComment(CommentRequest commentRequest);
    public void editContent(UpdateCommentRequest commentRequest);
    public List<CommentResponse> getListCommentByVideoId(long videoId);
}
