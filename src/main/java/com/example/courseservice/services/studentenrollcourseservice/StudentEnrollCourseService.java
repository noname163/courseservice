package com.example.courseservice.services.studentenrollcourseservice;

import java.util.List;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.StudentEnrollRequest;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;

public interface StudentEnrollCourseService {
    public PaginationResponse<List<CourseResponse>> getListCourse(Integer page,
            Integer size, String field, SortType sortType);
    
    public List<Long> getListCourseId(String studentEmail);

    public List<Long> getListCourseStudentNotEnrolled(String studentEmail, List<Course> courses);

    public List<Long> getListVideoIdStudentAccess(String email,List<Course> courses);

    public boolean isStudentEnrolled(String studentEmail, Long CourseId);

    public void insertStudentEnroll(StudentEnrollRequest studentEnrollRequest);
}
