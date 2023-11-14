package com.example.courseservice.services.courseservice.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.CourseFilter;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.constants.VerifyStatus;
import com.example.courseservice.data.dto.request.CourseRequest;
import com.example.courseservice.data.dto.request.VerifyRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseDetailResponse;
import com.example.courseservice.data.dto.response.CourseResponse;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.Level;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.mappers.CourseMapper;
import com.example.courseservice.mappers.VideoCourseMapper;
import com.example.courseservice.services.courseservice.CourseService;
import com.example.courseservice.services.coursetmpservice.CourseTmpService;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.levelservice.LevelService;
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
    @Autowired
    private CourseTopicService courseTopicService;
    @Autowired
    private CourseTmpService courseTmpService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private VideoCourseMapper videoCourseMapper;

    private final Double maxRate = 5d;

    @Override
    public void createCourse(CourseRequest courseRequest, MultipartFile thumbnail) {
        Level level = levelService.getLevel(courseRequest.getLevelId());
        FileResponse fileResponse = fileService.fileStorage(thumbnail);
        CloudinaryUrl thumbinial = uploadService.uploadMedia(fileResponse);
        Course course = courseMapper.mapDtoToEntity(courseRequest);
        course.setThumbnial(thumbinial.getUrl());
        course.setLevel(level);
        course.setCommonStatus(CommonStatus.WAITING);
        course.setCourseTopics(courseTopicService.courseTopicsByString(courseRequest.getTopic()));
        courseRepository.save(course);
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourseByEmail(String email, Integer page, Integer size,
            String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        Page<Course> listSubject = courseRepository.findCourseByTeacherEmail(email, pageable);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(listSubject.getContent()))
                .totalPage(listSubject.getTotalPages())
                .totalRow(listSubject.getTotalElements())
                .build();
    }

    @Override
    public PaginationResponse<List<CourseResponse>> getListCourse(CommonStatus commonStatus, Integer page, Integer size,
            String field,
            SortType sortType) {

        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);

        if (commonStatus.equals(CommonStatus.ALL)) {
            Page<Course> listSubject = courseRepository.findAll(pageable);
            return PaginationResponse.<List<CourseResponse>>builder()
                    .data(courseMapper.mapEntitiesToDtos(listSubject.getContent()))
                    .totalPage(listSubject.getTotalPages())
                    .totalRow(listSubject.getTotalElements())
                    .build();
        }

        Page<Course> listSubject = courseRepository.findByCommonStatus(pageable, commonStatus);

        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(listSubject.getContent()))
                .totalPage(listSubject.getTotalPages())
                .totalRow(listSubject.getTotalElements())
                .build();
    }

    @Override
    public CourseDetailResponse getCourseDetail(long id, CommonStatus commonStatus) {
        Course course = courseRepository
                .findByIdAndCommonStatus(id, commonStatus)
                .orElseThrow(() -> new BadRequestException("Cannot find course with id " + id));

        CourseDetailResponse courseDetailResponse = courseMapper.mapCourseDetailEntityToDto(course);

        if (!course.getVideos().isEmpty()) {
            List<Video> videos = course.getVideos()
                    .stream()
                    .filter(video -> video.getStatus().equals(CommonStatus.AVAILABLE))
                    .collect(Collectors.toList());

            List<CourseVideoResponse> courseVideoResponses = videoCourseMapper.mapEntitiesToDtos(videos);
            courseDetailResponse.setVideoResponse(courseVideoResponses);
        }

        courseDetailResponse.setCourseResponse(courseMapper.mapEntityToDto(course));
        return courseDetailResponse;
    }

    @Override
    public PaginationResponse<List<CourseResponse>> filterCourseBy(CourseFilter filterBy, CommonStatus commonStatus,
            List<String> value,
            Integer page,
            Integer size, String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        PaginationResponse<List<CourseResponse>> result = PaginationResponse.<List<CourseResponse>>builder().build();
        switch (filterBy) {
            case SUBJECT:
                Page<Course> course = courseRepository.findByCommonStatusAndSubjectIn(pageable, commonStatus,
                        value);
                result.setData(courseMapper.mapEntitiesToDtos(course.getContent()));
                result.setTotalPage(course.getTotalPages());
                result.setTotalRow(course.getNumberOfElements());
                break;
            case RATE:
                result = filterCourseByRate(value, commonStatus, pageable);
                break;
            case PRICE:
                result = filterByPrice(value, commonStatus, pageable);
                break;
            case LEVEL:
                result = filterCourseByLevel(value, commonStatus, pageable);
                break;
            default:
                result = getListCourse(commonStatus, page, size, field, sortType);
                break;
        }
        return result;
    }

    private PaginationResponse<List<CourseResponse>> filterCourseByRate(List<String> value, CommonStatus commonStatus,
            Pageable pageable) {
        if (value.size() > 1) {
            throw new BadRequestException("Invalid data require for filter by rate");
        }
        Double minRate = Double.parseDouble(value.stream().findFirst().get());
        if (minRate < 0) {
            throw new BadRequestException("Rate cannot smaller than 0");
        }
        Page<Course> courses = courseRepository.findByCommonStatusAndAverageRateBetween(commonStatus, minRate,
                maxRate, pageable);
        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(courses.getContent()))
                .totalPage(courses.getTotalPages())
                .totalRow(courses.getTotalElements())
                .build();
    }

    private PaginationResponse<List<CourseResponse>> filterCourseByLevel(List<String> value, CommonStatus commonStatus,
            Pageable pageable) {
        if (value.size() > 3) {
            throw new BadRequestException("Invalid data require for filter by rate");
        }
        List<Long> levelIds = value
                .stream()
                .map(id -> Long.parseLong(id))
                .collect(Collectors.toList());
        List<Level> levels = levelService.getListLevel(levelIds);
        Page<Course> courses = courseRepository.findByCommonStatusAndLevelIn(pageable, commonStatus, levels);
        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(courses.getContent()))
                .totalPage(courses.getTotalPages())
                .totalRow(courses.getTotalElements())
                .build();
    }

    private PaginationResponse<List<CourseResponse>> filterByPrice(List<String> value, CommonStatus commonStatus,
            Pageable pageable) {
        if (value.size() > 2) {
            throw new BadRequestException("Invalid data for filter by price");
        }
        Double price1 = Double.parseDouble(value.get(0));
        Double price2 = Double.parseDouble(value.get(1));

        Double min = Math.min(price1, price2);
        Double max = Math.max(price1, price2);
        Page<Course> courses = courseRepository.findByCommonStatusAndPriceBetween(commonStatus, min,
                max, pageable);
        return PaginationResponse.<List<CourseResponse>>builder()
                .data(courseMapper.mapEntitiesToDtos(courses.getContent()))
                .totalPage(courses.getTotalPages())
                .totalRow(courses.getTotalElements())
                .build();

    }

    @Override
    public void verifyCourse(VerifyRequest verifyRequest) {
        Course course = courseRepository
                .findById(verifyRequest.getId())
                .orElseThrow(() -> new BadRequestException("Cannot find course with id " + verifyRequest.getId()));
        if (!courseTmpService.isUpdate(verifyRequest.getId())) {
            if (course.getCommonStatus() == CommonStatus.AVAILABLE
                    && verifyRequest.getVerifyStatus().equals(VerifyStatus.ACCEPTED)) {
                throw new BadRequestException("There is no difference to change");
            }
            if (course.getCommonStatus() == CommonStatus.REJECT
                    && verifyRequest.getVerifyStatus().equals(VerifyStatus.REJECT)) {
                throw new BadRequestException("There is no difference to change");
            }
            if (VerifyStatus.ACCEPTED.equals(verifyRequest.getVerifyStatus())) {
                course.setCommonStatus(CommonStatus.AVAILABLE);
            } else {
                course.setCommonStatus(CommonStatus.REJECT);
            }
            courseRepository.save(course);
        }
        if (VerifyStatus.ACCEPTED.equals(verifyRequest.getVerifyStatus())) {
            courseTmpService.inserCourseTmpToReal(verifyRequest.getId());
        }

    }

    @Override
    public Course getCourseByIdAndEmail(Long id, String email) {
        return courseRepository
                .findCourseByTeacherEmailAndId(email, id)
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found course belong to email " + email + " with id " + id));
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found course with id " + id));
    }

    @Override
    public boolean isCourseBelongTo(String email, long courseId) {
        return courseRepository.existsByTeacherEmailAndId(email, courseId);
    }

}
