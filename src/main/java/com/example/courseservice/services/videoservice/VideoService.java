package com.example.courseservice.services.videoservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.CourseVideoResponse;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoDetailResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Video;
import com.example.courseservice.data.object.VideoUpdate;

public interface VideoService {
    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial,
            MultipartFile material);

    public void updateVideo(VideoUpdateRequest videoUpdateRequest, MultipartFile video, MultipartFile thumbinal, MultipartFile material); 

    public void insertVideoUrl(VideoUpdate videoUpdate);

    public Video getVideoByIdAndCommonStatus(Long videoId, CommonStatus commonStatus);

    public Video getVideoByIdAndCommonStatusNot(Long videoId, CommonStatus commonStatus);

    public PaginationResponse<List<VideoAdminResponse>> getVideoForAdmin(CommonStatus commonStatus, Integer page,
            Integer size, String field, SortType sortType);

    public PaginationResponse<List<VideoAdminResponse>> getVideoForTeacher(CommonStatus commonStatus,
            Integer page,
            Integer size, String field, SortType sortType);

    public PaginationResponse<List<VideoItemResponse>> getVideoForUser(String email,
            Integer page,
            Integer size, String field, SortType sortType);

    public VideoDetailResponse getAvailableVideoDetailById(Long videoId, CommonStatus commonStatus);

    public VideoDetailResponse getVideoDetailByIdExcept(Long videoId, CommonStatus commonStatus);

    public PaginationResponse<List<VideoItemResponse>> getListVideoAvailableByCourse(Long courseId, Integer page,
            Integer size, String field, SortType sortType);

    public void updateVideoOrder(List<VideoOrder> videoOrders, Long courseId);

    public VideoResponse uploadVideoByCourse(VideoRequest videoRequest, MultipartFile video,
            MultipartFile thumbnail);

    public void deleteVideo(Long videoId);

    public List<CourseVideoResponse> getVideoByCourseIdAndCommonStatus(Long courseId, CommonStatus commonStatus);

    public PaginationResponse<List<VideoAdminResponse>> getVideoByCourseId(Long courseId, Integer page,
            Integer size, String field, SortType sortType);
}
