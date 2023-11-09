package com.example.courseservice.services.commentservice.impl;

import org.springframework.stereotype.Service;

import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.response.CommentResponse;
import com.example.courseservice.services.commentservice.CommentService;

@Service
public class CommentServiceImpl implements CommentService {

    @Override
    public void createComment(CommentRequest commentRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createComment'");
    }

    @Override
    public void editContent(CommentRequest commentRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editContent'");
    }

    @Override
    public CommentResponse getListCommentByVideoId(long videoId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getListCommentByVideoId'");
    }
    
}
