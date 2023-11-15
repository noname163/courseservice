package com.example.courseservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.courseservice.data.dto.response.RatingResponse;
import com.example.courseservice.data.entities.Rating;

@Component
public class RatingMapper {
    public RatingResponse mapEntityToDto(Rating rating) {
        return RatingResponse
                .builder()
                .content(rating.getComment())
                .rate(rating.getRate())
                .fullName(rating.getStudentName())
                .build();
    }

    public List<RatingResponse> mapEntitiesToDtos(List<Rating> ratings){
        return ratings.stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }
}
