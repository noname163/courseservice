package com.example.courseservice.services.courseservice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.mappers.CourseMapper;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private UploadService uploadService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired 
    private PageableUtil pageableUtil;
    @Override
    public void createCourse(CourseRequest courseRequest) {
        FileResponse fileResponse = fileService.fileStorage(courseRequest.getThumbinial());
        CloudinaryUrl thumbinial = uploadService.uploadMedia(fileResponse);
        Course course = courseMapper.mapDtoToEntity(courseRequest);
        course.setThumbinial(thumbinial.getUrl());
        course.setCommonStatus(CommonStatus.AVAILABLE);
        courseRepository.save(course);
    }
    @Override
    public PaginationResponse<List<CourseResponse>> getListCourseByEmail(String email, Integer page, Integer size,
            String field, SortType sortType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getListCourseByEmail'");
    }
    @Override
    public PaginationResponse<List<CourseResponse>> getListCourse(Integer page, Integer size, String field, SortType sortType) {
        
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<Course> listSubject = courseRepository.findByCommonStatus(pageable, CommonStatus.AVAILABLE);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(listSubject.getContent()))
                .totalPage(listSubject.getTotalPages())
                .totalRow(listSubject.getTotalElements())
                .build();
    }
   
}
