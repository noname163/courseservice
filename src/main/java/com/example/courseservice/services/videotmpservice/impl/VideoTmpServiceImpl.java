package com.example.courseservice.services.videotmpservice.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoTemporaryUpdateRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.entities.CourseTemporary;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.entities.VideoTemporary;
import com.example.courseservice.data.object.UserInformation;
import com.example.courseservice.data.object.VideoUpdate;
import com.example.courseservice.data.repositories.CourseRepository;
import com.example.courseservice.data.repositories.CourseTemporaryRepository;
import com.example.courseservice.data.repositories.VideoRepository;
import com.example.courseservice.data.repositories.VideoTemporaryRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.InValidAuthorizationException;
import com.example.courseservice.mappers.VideoTemporaryMapper;
import com.example.courseservice.services.authenticationservice.SecurityContextService;
import com.example.courseservice.services.coursetopicservice.CourseTopicService;
import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.services.uploadservice.UploadService;
import com.example.courseservice.services.videotmpservice.VideoTmpService;
import com.example.courseservice.utils.PageableUtil;

@Service
public class VideoTmpServiceImpl implements VideoTmpService {

    @Autowired
    private VideoTemporaryRepository videoTemporaryRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    @Lazy
    private CourseRepository courseRepository;
    @Autowired
    private VideoTemporaryMapper videoTemporaryMapper;
    @Autowired
    private CourseTemporaryRepository courseTemporaryRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private UploadService uploadService;
    @Autowired
    @Lazy
    private CourseTopicService courseTopicService;

    @Override
    public VideoResponse updateVideo(VideoUpdateRequest videoUpdateRequest, MultipartFile video,
            MultipartFile thumbnail, MultipartFile material) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        Course course = courseRepository
                .findByIdAndCommonStatusNot(videoUpdateRequest.getCourseId(), CommonStatus.DELETED)
                .orElseThrow(
                        () -> new BadRequestException("Not exist video with id "
                                + videoUpdateRequest.getCourseId()));

        if (!course.getTeacherEmail().equals(currentUser.getEmail())) {
            throw new InValidAuthorizationException("Not this course to create video");
        }
        course.setCommonStatus(CommonStatus.UNAVAILABLE);
        courseRepository.save(course);

        CourseTemporary courseTemporary = courseTemporaryRepository.save(CourseTemporary
                .builder()
                .updateTime(LocalDateTime.now())
                .status(CommonStatus.UPDATING)
                .course(course)
                .name(course.getName())
                .price(course.getPrice())
                .description(course.getDescription())
                .teacherEmail(course.getTeacherEmail())
                .teacherName(currentUser.getFullname())
                .teacherId(currentUser.getId())
                .thumbnial(course.getThumbnial())
                .subject(course.getSubject())
                .levelId(course.getLevel().getId())
                .build());
        courseTopicService.addCourseTemporaryToTopic(course, courseTemporary);
        VideoTemporary videoInsert = videoTemporaryRepository.save(
                VideoTemporary
                        .builder()
                        .course(course)
                        .name(videoUpdateRequest.getName())
                        .description(videoUpdateRequest.getDescription())
                        .videoStatus(videoUpdateRequest.getVideoStatus())
                        .courseTemporary(courseTemporary)
                        .createDate(LocalDateTime.now())
                        .status(CommonStatus.UPDATING)
                        .videoStatus(videoUpdateRequest.getVideoStatus())
                        .build());
        FileResponse videoFile = fileService.fileStorage(video);
        FileResponse thumbnialFile = fileService.fileStorage(thumbnail);
        VideoResponse videoResponse = VideoResponse
                .builder()
                .videoId(videoInsert.getId())
                .video(videoFile)
                .thumbnail(thumbnialFile)
                .build();
        if (material != null) {
            FileResponse materialFile = fileService.fileStorage(material);
            videoResponse.setMaterial(materialFile);
        }
        return videoResponse;
    }

    @Override
    public void insertVideoUrl(VideoUpdate videoUpdate) {
        if (videoUpdate.getThumbnailUrl() == null
                || videoUpdate.getThumbnailUrl().equals("")
                || videoUpdate.getVideoId() < 0
                || videoUpdate.getVideoId() == null
                || videoUpdate.getVideoUrl().equals("")
                || videoUpdate.getVideoUrl() == null) {
            throw new BadRequestException("Data not valid");
        }
        VideoTemporary video = videoTemporaryRepository
                .findById(videoUpdate.getVideoId())
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found Video temporary with id " + videoUpdate.getVideoId()));
        video.setUrlVideo(videoUpdate.getVideoUrl());
        video.setUrlThumbnail(videoUpdate.getThumbnailUrl());
        if (videoUpdate.getMaterial() != null) {
            video.setUrlMaterial(videoUpdate.getMaterial());
        }
        videoTemporaryRepository.save(video);
    }

    @Override
    public void insertVideoTmpToReal(Long courseTeporaryId, Course course) {
        List<VideoTemporary> videoTemporaries = videoTemporaryRepository
                .findByCourseTemporaryIdAndStatusNot(courseTeporaryId, CommonStatus.DELETED);
        if (!videoTemporaries.isEmpty()) {
            List<Video> videos = videoTemporaryMapper.mapVideosTmpToReal(videoTemporaries, course);
            videoRepository.saveAll(videos);
            videoTemporaryRepository.deleteAll(videoTemporaries);
        }
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getUpdateVideo(Integer page, Integer size, String field,
            SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Page<VideoTemporary> videotemp = videoTemporaryRepository.findAll(pageable);
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoTemporaryMapper.mapVideoItemResponses(videotemp.getContent()))
                .totalPage(videotemp.getTotalPages())
                .totalRow(videotemp.getTotalElements())
                .build();
    }

    @Override
    public boolean isUpdate(Long videoId) {
        return videoTemporaryRepository.existsByVideoId(videoId);
    }

    @Override
    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial,
            MultipartFile material) {
        CourseTemporary course = courseTemporaryRepository
                .findByIdAndStatusNot(videoRequest.getCourseId(), CommonStatus.DELETED)
                .orElseThrow(() -> new BadRequestException(
                        "Not exist video with id " + videoRequest.getCourseId()));
        Integer maxOrdinalNumber = videoTemporaryRepository.findMaxOrdinalNumberByCourse(course);

        // Set the ordinalNumber for the new video
        int ordinalNumber = maxOrdinalNumber != null ? maxOrdinalNumber + 1 : 1;
        VideoTemporary videoConvert = videoTemporaryMapper.mapDtoToEntity(videoRequest);
        videoConvert.setStatus(CommonStatus.DRAFT);
        videoConvert.setCourseTemporary(course);
        videoConvert.setOrdinalNumber(ordinalNumber);
        VideoTemporary videoInsert = videoTemporaryRepository.save(videoConvert);
        FileResponse videoFile = fileService.fileStorage(video);
        FileResponse thumbnialFile = fileService.fileStorage(thumbnial);
        VideoResponse videoResponse = VideoResponse
                .builder()
                .videoId(videoInsert.getId())
                .video(videoFile)
                .thumbnail(thumbnialFile)
                .build();
        if (material != null) {
            FileResponse materialFile = fileService.fileStorage(material);
            videoResponse.setMaterial(materialFile);
        }
        return videoResponse;
    }

    @Override
    public List<VideoItemResponse> getVideoTemporaryByCourseTemporaryIdForAdmin(Long courseTemporaryId) {
        List<VideoTemporary> videoTemporaries = videoTemporaryRepository
                .findByCourseTemporaryIdAndStatus(courseTemporaryId, CommonStatus.DRAFT);
        return videoTemporaryMapper.mapVideoItemResponses(videoTemporaries);
    }

    @Override
    public void updateVideoOrder(List<VideoOrder> videoOrders, Long courseId) {

        Set<Long> videoIds = videoOrders.stream()
                .map(VideoOrder::getVideoId)
                .collect(Collectors.toSet());

        List<VideoTemporary> videosToUpdate = videoTemporaryRepository.findByCourseTemporaryIdAndIdIn(courseId,
                videoIds);

        Map<Long, VideoTemporary> videoMap = videosToUpdate.stream()
                .collect(Collectors.toMap(VideoTemporary::getId, Function.identity()));

        List<VideoTemporary> updatedVideos = videoOrders.stream()
                .filter(videoOrder -> videoMap.containsKey(videoOrder.getVideoId()))
                .map(videoOrder -> {
                    VideoTemporary video = videoMap.get(videoOrder.getVideoId());
                    video.setUpdateTime(LocalDateTime.now());
                    video.setOrdinalNumber(videoOrder.getVideoOrder());
                    return video;
                })
                .collect(Collectors.toList());

        videoTemporaryRepository.saveAll(updatedVideos);
    }

    @Override
    public List<VideoItemResponse> getVideoTemporaryByCourseTemporaryIdForTeacher(Long courseTemporaryId) {
        Long userId = securityContextService.getCurrentUser().getId();

        if (Boolean.FALSE.equals(courseTemporaryRepository.existsByTeacherIdAndId(userId, courseTemporaryId))) {
            throw new InValidAuthorizationException("Owner permission require");
        }

        List<VideoTemporary> videoTemporaries = videoTemporaryRepository
                .findByCourseTemporaryIdAndStatus(courseTemporaryId, CommonStatus.DRAFT);
        return videoTemporaryMapper.mapVideoItemResponses(videoTemporaries);
    }

    @Override
    public PaginationResponse<List<VideoItemResponse>> getUpdateVideoForCurrentUser(Integer page, Integer size,
            String field, SortType sortType) {
        Pageable pageable = pageableUtil.getPageable(page, size, field, sortType);
        Long teacherId = securityContextService.getCurrentUser().getId();
        Page<VideoTemporary> videotemp = videoTemporaryRepository.findVideosByTeacherId(teacherId, pageable);
        return PaginationResponse.<List<VideoItemResponse>>builder()
                .data(videoTemporaryMapper.mapVideoItemResponses(videotemp.getContent()))
                .totalPage(videotemp.getTotalPages())
                .totalRow(videotemp.getTotalElements())
                .build();
    }

    @Override
    public List<CourseVideoResponse> getCourseVideoResponseById(Long id) {
        List<VideoTemporary> videoTemporaries = videoTemporaryRepository.findByCourseTemporaryId(id);

        return videoTemporaryMapper.mapToCourseVideosResponse(videoTemporaries);
    }

    @Override
    public VideoResponse editVideoTmpById(VideoTemporaryUpdateRequest videoUpdateRequest, MultipartFile video,
            MultipartFile thumbnial, MultipartFile material) {
        UserInformation currentUser = securityContextService.getCurrentUser();
        VideoTemporary videoTemporary = videoTemporaryRepository.findById(videoUpdateRequest.getVideoTemporaryId())
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found video temporary with Id " + videoUpdateRequest.getVideoTemporaryId()));
        CourseTemporary courseTemporary = videoTemporary.getCourseTemporary();
        if (!courseTemporary.getTeacherEmail().equals(currentUser.getEmail())) {
            throw new InValidAuthorizationException("You need owner permission to edit");
        }
        videoTemporary.setDescription(
                Optional.ofNullable(videoUpdateRequest.getDescription()).orElse(videoTemporary.getDescription()));
        videoTemporary.setName(Optional.ofNullable(videoUpdateRequest.getName()).orElse(videoTemporary.getName()));
        videoTemporary.setVideoStatus(
                Optional.ofNullable(videoUpdateRequest.getVideoStatus()).orElse(videoTemporary.getVideoStatus()));
        videoTemporaryRepository.save(videoTemporary);
        VideoResponse videoResponse = VideoResponse
                .builder()
                .videoId(videoTemporary.getId())
                .build();
        if (video != null) {
            FileResponse videoFile = fileService.fileStorage(video);
            videoResponse.setVideo(videoFile);
        }
        if (thumbnial != null) {
            FileResponse thumbinal = fileService.fileStorage(thumbnial);
            videoResponse.setThumbnail(thumbinal);
        }
        if (material != null) {
            FileResponse materialFile = fileService.fileStorage(material);
            videoResponse.setMaterial(materialFile);
        }
        return videoResponse;
    }

    @Override
    public void uploadEditVideoTemporaryFile(VideoResponse videoResponse) {
        VideoTemporary video = videoTemporaryRepository
                .findById(videoResponse.getVideoId())
                .orElseThrow(() -> new BadRequestException(
                        "Cannot found Video temporary with id " + videoResponse.getVideoId()));
        if (videoResponse.getVideo() != null) {
            CloudinaryUrl cloudinaryUrl = uploadService.uploadMedia(videoResponse.getVideo());
            video.setUrlVideo(cloudinaryUrl.getUrl());
            video.setDuration(cloudinaryUrl.getDuration());
        }
        if (videoResponse.getThumbnail() != null) {
            CloudinaryUrl cloudinaryUrl = uploadService.uploadMedia(videoResponse.getThumbnail());
            video.setUrlThumbnail(cloudinaryUrl.getUrl());
        }
        if (videoResponse.getMaterial() != null) {
            CloudinaryUrl cloudinaryUrl = uploadService.uploadMetrial(videoResponse.getMaterial());
            video.setUrlMaterial(cloudinaryUrl.getUrl());
        }
        videoTemporaryRepository.save(video);
    }

}
