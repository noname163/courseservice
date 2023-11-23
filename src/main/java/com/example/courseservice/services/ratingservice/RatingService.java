package com.example.courseservice.services.ratingservice;

import java.util.List;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.RatingRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.RatingResponse;

public interface RatingService {
    public void createRating(RatingRequest ratingRequest);
    public boolean updateRating(RatingRequest ratingRequest);
    public PaginationResponse<List<RatingResponse>> getRatingByCourse(Long courseId, Integer page,
            Integer size, String field, SortType sortType);
}
