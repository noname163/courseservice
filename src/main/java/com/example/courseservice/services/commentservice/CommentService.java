package com.example.courseservice.services.commentservice;

import java.util.List;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.request.UpdateCommentRequest;
import com.example.courseservice.data.dto.response.CommentResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;

public interface CommentService {
    public void createComment(CommentRequest commentRequest);
    public void editContent(UpdateCommentRequest commentRequest);
    public PaginationResponse<List<CommentResponse>> getListCommentByVideoId(long videoId, Integer page, Integer size,
            String field, SortType sortType);
}
