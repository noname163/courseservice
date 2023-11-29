package com.example.courseservice.services.videotmpservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoTemporaryUpdateRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.object.VideoUpdate;

public interface VideoTmpService {
    public VideoResponse updateVideo(VideoUpdateRequest videoUpdateRequest, MultipartFile video,
            MultipartFile thumbnail, MultipartFile material);

    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial, MultipartFile material);

    public void insertVideoUrl(VideoUpdate videoUpdate);

    public PaginationResponse<List<VideoItemResponse>> getUpdateVideo(Integer page,
            Integer size, String field, SortType sortType);

    public PaginationResponse<List<VideoItemResponse>> getUpdateVideoForCurrentUser(Integer page,
            Integer size, String field, SortType sortType);

    public boolean isUpdate(Long videoId);

    public void insertVideoTmpToReal(Long courseTemporary, Course course);

    public List<VideoItemResponse> getVideoTemporaryByCourseTemporaryIdForTeacher(Long courseTemporaryId);

    public List<VideoItemResponse> getVideoTemporaryByCourseTemporaryIdForAdmin(Long courseTemporaryId);

    public List<CourseVideoResponse> getCourseVideoResponseById(Long id);

    public void updateVideoOrder(List<VideoOrder> videoOrders, Long courseId);

    public VideoResponse editVideoTmpById(VideoTemporaryUpdateRequest videoTemporaryUpdateRequest, MultipartFile video, MultipartFile thumbnial, MultipartFile material);
    
    public void uploadEditVideoTemporaryFile(VideoResponse videoResponse);

    public VideoAdminResponse getVideoTemporaryById(Long id);

    public void deletedTemporaryVideo(Long id);
    
    public void deletedTemporaryVideoByCourseTmpId(Long id);
}
