// package com.example.courseservice.services.courseservice.impl;

// import com.example.courseservice.data.constants.CommonStatus;
// import com.example.courseservice.data.constants.SortType;
// import com.example.courseservice.data.dto.request.CourseRequest;
// import com.example.courseservice.data.dto.response.CourseResponse;
// import com.example.courseservice.data.dto.response.FileResponse;
// import com.example.courseservice.data.entities.Course;
// import com.example.courseservice.data.repositories.CourseRepository;
// import com.example.courseservice.services.courseservice.impl.CourseServiceImpl;
// import com.example.courseservice.utils.PageableUtil;

// import java.io.IOException;
// import java.util.Arrays;
// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.multipart.MultipartFile;

// public class CourseServiceImplTest {

//     @Mock
//     private CourseRepository courseRepository;

//     @Mock
//     private PageableUtil pageableUtil;

//     @InjectMocks
//     private CourseServiceImpl courseService;

//     @BeforeEach
//     public void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     public void testGetListCourseByEmail() {
//         String email = "sample@example.com";
//         Integer page = 1;
//         Integer size = 10;

//         Pageable pageable = createSamplePageable();
//         Page<Course> samplePage = createSampleCoursePage();

//         // Mock the behavior of pageableUtil and courseRepository
//         Mockito.when(pageableUtil.getPageable(page, size, "field", SortType.ASC)).thenReturn(pageable);
//         Mockito.when(courseRepository.findCourseByTeacherEmail(email, pageable)).thenReturn(samplePage);

//         // Perform the getListCourseByEmail operation
//         courseService.getListCourseByEmail(email, page, size, "field", SortType.ASC);

//         // Verify that courseRepository.findCourseByTeacherEmail was called
//         Mockito.verify(courseRepository, Mockito.times(1)).findCourseByTeacherEmail(email, pageable);
//     }

//     private Pageable createSamplePageable() {
//         Sort sort = Sort.by(Sort.Order.asc("test"));
//         return PageRequest.of(1, 12, sort);
//     }

//     private Page<Course> createSampleCoursePage() {
//     // Replace this with your actual list of courses or mock data
//     Course course = Course.builder().build();
//     List<Course> sampleCourses = Arrays.asList(course);

//     Page<Course> sampleCoursePage = new PageImpl<>(sampleCourses, PageRequest.of(0, sampleCourses.size()), sampleCourses.size());

//     return sampleCoursePage;
// }
// }
