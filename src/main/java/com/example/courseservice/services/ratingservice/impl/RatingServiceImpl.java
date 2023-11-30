package com.example.courseservice.services.ratingservice.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.RatingRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.RatingResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Rating;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.RatingRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.RatingMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.ratingservice.RatingService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private RatingMapper ratingMapper;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentEnrollCourseService studentEnrollCourseService;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public void createRating(RatingRequest ratingRequest) {
        UserInformation currentUser = securityContextService.getCurrentUser();

        if (!studentEnrollCourseService.isStudentEnrolled(currentUser.getEmail(), ratingRequest.getCourseId())) {
            throw new BadRequestException("User must owner course to rate");
        }
        if (!updateRating(ratingRequest)) {
            Course course = courseService.getCourseById(ratingRequest.getCourseId());

            ratingRepository.save(Rating
                    .builder()
                    .comment(ratingRequest.getContent())
                    .createdDate(LocalDateTime.now())
                    .userAvatar(currentUser.getAvatar())
                    .rate(ratingRequest.getRating())
                    .studentId(currentUser.getId())
                    .studentName(currentUser.getFullname())
                    .course(course)
                    .build());
        }
    }

    @Override
    public boolean updateRating(RatingRequest ratingRequest) {
        UserInformation currentUser = securityContextService.getCurrentUser();

        Optional<Rating> ratingOpt = ratingRepository
                .findByStudentIdAndCourseId(currentUser.getId(), ratingRequest.getCourseId());
        if (ratingOpt.isEmpty()) {
            return false;
        }
        Rating rating = ratingOpt.get();
        rating.setRate(Optional.ofNullable(ratingRequest.getRating()).orElse(rating.getRate()));
        rating.setUpdateTime(LocalDateTime.now());
        rating.setUserAvatar(currentUser.getAvatar());
        rating.setComment(Optional.ofNullable(ratingRequest.getContent()).orElse(rating.getComment()));
        ratingRepository.save(rating);
        return true;

    }

    @Override
    public PaginationResponse<List<RatingResponse>> getRatingByCourse(Long courseId, Integer page, Integer size,
            String field,
            SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<Rating> ratings = ratingRepository.findByCourseId(courseId, pageable);
        return PaginationResponse.<List<RatingResponse>>builder()
                .data(ratingMapper.mapEntitiesToDtos(ratings.getContent()))
                .totalPage(ratings.getTotalPages())
                .totalRow(ratings.getTotalElements())
                .build();
    }

}
