package com.example.courseservice.services.studentenrollcourseservice.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.StudentEnrolledStatus;
import com.example.courseservice.data.dto.request.StudentEnrollRequest;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.StudentEnrolledCourses;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.StudentEnrolledCoursesRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.CourseMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.studentenrollcourseservice.StudentEnrollCourseService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class StudentEnrollCourseServiceImpl implements StudentEnrollCourseService {
    @Autowired
    private StudentEnrolledCoursesRepository studentEnrolledCoursesRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourse(Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        UserInformation userInformation = securityContextService.getCurrentUser();
        Page<StudentEnrolledCourses> studentEnrollCourse = studentEnrolledCoursesRepository.findByStudentEmail(
                userInformation.getEmail(),
                pageable);
        List<CourseResponse> courseResponses = studentEnrollCourse.getContent()
                .stream()
                .map(studentEnrolledCourse -> courseMapper.mapEntityToDto(studentEnrolledCourse.getCourse()))
                .collect(Collectors.toList());

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseResponses)
                .totalPage(studentEnrollCourse.getTotalPages())
                .totalRow(studentEnrollCourse.getTotalElements())
                .build();
    }

    @Override
    public boolean isStudentEnrolled(String studentEmail, Long CourseId) {
        Optional<StudentEnrolledCourses> studentEnrolledCourses = studentEnrolledCoursesRepository
                .findByStudentEmailAndCourseId(studentEmail, CourseId);
        if (studentEnrolledCourses.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void insertStudentEnroll(StudentEnrollRequest studentEnrollRequest) {
        Course course = courseRepository.findById(studentEnrollRequest.getCourseId())
                .orElseThrow(() -> new BadRequestException(
                        "Not exist course with id " + studentEnrollRequest.getCourseId()));

        studentEnrolledCoursesRepository.save(StudentEnrolledCourses
                .builder()
                .studentEmail(studentEnrollRequest.getEmail())
                .studentId(studentEnrollRequest.getStudentId())
                .status(StudentEnrolledStatus.ENROLLED)
                .course(course)
                .build());
    }

    @Override
    public List<Long> getListCourseId(String studentEmail) {
        
        List<StudentEnrolledCourses> courseEnrolled = studentEnrolledCoursesRepository.findByStudentEmail(studentEmail);

        return courseEnrolled.stream()
                .map(enrolledCourse -> enrolledCourse.getCourse().getId())
                .collect(Collectors.toList());
    }

@Override
public List<Long> getListCourseStudentNotEnrolled(String studentEmail, List<Course> courses) {
        return studentEnrolledCoursesRepository.filterCourseStudentNotEnrolled(studentEmail, courses);
}

@Override
public List<Long> getListVideoIdStudentAccess(String email, List<Course> courses) {
        return studentEnrolledCoursesRepository.getVideoIdsByStudentEmailAndCourses(email, courses, CommonStatus.AVAILABLE);
}


}
