package com.example.courseservice.services.coursetmpservice.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseTemporaryRepository;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetmpservice.CourseTmpService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.uploadservice.UploadService;
import com.netflix.discovery.converters.Auto;

@Service
public class CourseTmpServiceImpl implements CourseTmpService {
    @Autowired
    private CourseTemporaryRepository courseTemporaryRepository;
    @Autowired
    private CourseService courseService;
    @Autowired
    private FileService fileService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public void insertTmpCourse(CourseUpdateRequest courseUpdateRequest, MultipartFile thumbnail) {
        
        UserInformation currentUser = securityContextService.getCurrentUser();
        Course course = courseService.getCourseByIdAndEmail(courseUpdateRequest.getCourseId(), currentUser.getEmail());
        FileResponse fileResponse = fileService.fileStorage(thumbnail);
        CloudinaryUrl thumbnial = uploadService.uploadMedia(fileResponse);
        
        CourseTemporary courseTemporary = CourseTemporary.builder()
                .description(Optional.ofNullable(courseUpdateRequest.getDescription()).orElse(course.getDescription()))
                .price(Optional.ofNullable(courseUpdateRequest.getPrice()).filter(price -> price > 0)
                        .orElse(course.getPrice()))
                .levelId(Optional.ofNullable(courseUpdateRequest.getLevelId()).filter(levelId -> levelId > 0)
                        .orElse(course.getLevel().getId()))
                .name(Optional.ofNullable(courseUpdateRequest.getName()).orElse(course.getName()))
                .status(CommonStatus.UPDATING)
                .thumbnial(thumbnial.getUrl())
                .course(course)
                .build();

        courseTemporaryRepository.save(courseTemporary);
    }

}
