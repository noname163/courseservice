package com.example.courseservice.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.CourseFilter;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.CourseTemporaryUpdateRequest;
import com.example.courseservice.data.dto.request.CourseUpdateRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetmpservice.CourseTmpService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseTmpService courseTmpService;

    @Operation(summary = "Update courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update course successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/teacher/update")
    public ResponseEntity<Void> updateCourse(@Valid @RequestPart CourseUpdateRequest courseRequest,
            @RequestPart(required = false) MultipartFile thumbnail) {
        courseTmpService.updateRealCourse(courseRequest, thumbnail);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Create courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create course successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/teacher/create")
    public ResponseEntity<Void> createCourse(@Valid @RequestPart CourseRequest courseRequest,
            @RequestPart() MultipartFile thumbnail) {
        courseTmpService.createCourse(courseRequest, thumbnail);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Verify courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Verify course successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/verify-course")
    public ResponseEntity<Long> verifyCourse(@Valid @RequestBody VerifyRequest verifyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.verifyCourse(verifyRequest));
    }

    @Operation(summary = "Get courses for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/user")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> getCourses(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.getListCourse(CommonStatus.AVAILABLE, page, size, field, sortType));
    }

    @Operation(summary = "Get courses for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> getCoursesForAdmin(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "ALL") CommonStatus commonStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.getListCourseForAdmin(searchTerm, commonStatus, page, size, field,
                        sortType));
    }

    @Operation(summary = "Get courses verify for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/verify-list")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> getCoursesTemporaryForAdmin(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseTmpService.getCourseTmpAndStatusNot(searchTerm, CommonStatus.WAITING, page, size, field,
                        sortType));
    }

    @Operation(summary = "Filter course temporary for admin and teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @GetMapping("/teacher/course-temporary/filter")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> filterCoursesTemporaryForAdminAndTeacher(
            @RequestParam(required = true) CommonStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseTmpService.filterCourseTmpStatus(status, page, size, field,
                        sortType));
    }

    @Operation(summary = "Get courses waiting for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/teacher/waiting-list")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> getCoursesTemporaryForTeacher(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "ALL") CommonStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseTmpService.getCourseTmpByEmailAndStatusNot(searchTerm, status, page, size,
                        field, sortType));
    }

    @Operation(summary = "Get all courses for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/teacher/get-all")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> getAllCourse(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.getAllCourse(page, size, field, sortType));
    }

    @Operation(summary = "Get courses for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/teacher")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> getCoursesByTeacherEmail(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "ALL") CommonStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.getListCourseByEmail(searchTerm, status, page, size, field,
                        sortType));
    }

    @Operation(summary = "Get courses by teacher email for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/user/find-by-email")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> getCoursesByTeacherEmailForUser(
            @RequestParam(required = true) String email,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.getListCourseByEmailForUser(email, page, size, field, sortType));
    }

    @Operation(summary = "Search courses for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> searchCourse(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.searchCourse(searchTerm, page, size, field, sortType));
    }

    @Operation(summary = "Delete courses for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete course successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/teacher")
    public ResponseEntity<Void> deleteCourseForTeacher(
            @RequestParam(required = true) Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(summary = "Delete draft courses for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete draft course successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/teacher/draft")
    public ResponseEntity<Void> deleteDraftCourseForTeacher(
            @RequestParam(required = true) Long courseId) {
        courseTmpService.deleteDraftCourse(courseId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(summary = "Filter courses for student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/filter")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> filterCourseBy(
            @RequestParam(required = true) CourseFilter filterBy,
            @RequestParam(required = true) List<String> value,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.filterCourseBy(filterBy, CommonStatus.AVAILABLE, value, page, size,
                        field,
                        sortType));
    }

    @Operation(summary = "Get courses detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetailResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/detail")
    public ResponseEntity<CourseDetailResponse> getCoursesDetail(
            @RequestParam(required = true, defaultValue = "0") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.getCourseDetail(id, CommonStatus.AVAILABLE));
    }

    @Operation(summary = "Get draft courses detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get draft course detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetailResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/admin/detail/draft")
    public ResponseEntity<CourseDetailResponse> getCoursesDraftDetail(
            @RequestParam(required = true, defaultValue = "0") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseTmpService.getCourseDetail(id));
    }

    @Operation(summary = "Get courses detail for admin and teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetailResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/detail/teacher")
    public ResponseEntity<CourseDetailResponse> getCoursesDetailExcept(
            @RequestParam(required = true) Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.getCourseDetailExcept(id, CommonStatus.DELETED));
    }

    @Operation(summary = "Teacher request admin verify courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Send request successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/teacher/send-verify-request")
    public ResponseEntity<Void> requestVerifyCourses(@RequestBody @Valid List<Long> ids) {
        courseTmpService.requestVerifyCourses(ids);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Teacher request admin verify courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Send request successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/teacher/edit-waiting-course")
    public ResponseEntity<Void> editCoursesTemporary(
            @RequestPart @Valid CourseTemporaryUpdateRequest courseTemporaryUpdateRequest,
            @RequestPart(required = false) MultipartFile thumbnail) {
        courseTmpService.editTmpCourse(courseTemporaryUpdateRequest, thumbnail);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Search temporary courses for teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/teacher/search/temporary-course")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> searchTemporaryCourse(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseTmpService.searchTemporaryCourseForTeacher(searchTerm, page, size, field,
                        sortType));
    }

    @Operation(summary = "Filter course for user ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get course successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @GetMapping("/user/filter")
    public ResponseEntity<PaginationResponse<List<CourseResponse>>> filterCourse(
            @RequestParam(required = false) List<Long> subjectList,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRate,
            @RequestParam(required = false) Double maxRate,
            @RequestParam(required = false) List<Long> levelList,
            @RequestParam(required = false) List<Long> topicList,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String field,
            @RequestParam(required = false, defaultValue = "ASC") SortType sortType) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseService.filterCourseByMultiple(subjectList, minPrice, maxPrice, minRate,
                        maxRate, levelList,
                        topicList, page, size, field, sortType));
    }

}
