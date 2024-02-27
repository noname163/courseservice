package com.example.courseservice.services.videoservice;

import java.util.List;

import com.example.courseservice.data.constants.Common;
import com.example.courseservice.data.constants.CommonStatus;
import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.CloudinaryUrl;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.entities.Video;

public interface VideoService {
    public Video saveVideoInformation(VideoRequest videoRequest);

    public void saveVideoFile(Video video,CloudinaryUrl videoFile, CloudinaryUrl material, CloudinaryUrl thumbnail);

    public Video updateVideo(VideoUpdateRequest videoUpdateRequest); 

    public Video getVideoByIdAndCommonStatus(Long videoId, CommonStatus commonStatus);

    public Video getVideoByIdAndCommonStatusNot(Long videoId, CommonStatus commonStatus);

    public PaginationResponse<List<VideoItemResponse>> getVideoForAdmin(CommonStatus commonStatus, Integer page,
            Integer size, String field, SortType sortType);

    public PaginationResponse<List<VideoItemResponse>> getVideoForTeacher(CommonStatus commonStatus,
            Integer page,
            Integer size, String field, SortType sortType);

    public PaginationResponse<List<VideoItemResponse>> getVideoForUser(String email,
            Integer page,
            Integer size, String field, SortType sortType);

    public VideoItemResponse getVideoDetailById(Long videoId, CommonStatus commonStatus);

    public PaginationResponse<List<VideoItemResponse>> getListVideoAvailableByCourse(CommonStatus commonStatus,Long courseId, Integer page,
            Integer size, String field, SortType sortType);

    public void updateVideoOrder(List<VideoOrder> videoOrders, Long courseId);

    public void deleteVideo(Long videoId);

    public PaginationResponse<List<VideoAdminResponse>> getVideoByCourseId(Long courseId, Integer page,
            Integer size, String field, SortType sortType);
}
