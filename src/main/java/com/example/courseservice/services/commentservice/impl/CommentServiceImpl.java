package com.example.courseservice.services.commentservice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.dto.request.CommentRequest;
import com.example.courseservice.data.dto.response.CommentResponse;
import com.example.courseservice.data.repositories.CommentRepository;
import com.example.courseservice.services.commentservice.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void createComment(CommentRequest commentRequest) {
        
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
