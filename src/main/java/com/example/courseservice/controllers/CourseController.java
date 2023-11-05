package com.example.courseservice.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
}
