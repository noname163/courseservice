package com.example.courseservice.services.videotmpservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoOrder;
import com.example.courseservice.data.dto.request.VideoRequest;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoItemResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.Course;
import com.example.courseservice.data.object.VideoUpdate;

public interface VideoTmpService {
    public VideoResponse updateVideo(VideoUpdateRequest videoUpdateRequest, MultipartFile video,
            MultipartFile thumbnail);

    public VideoResponse saveVideo(VideoRequest videoRequest, MultipartFile video, MultipartFile thumbnial);

    public void insertVideoUrl(VideoUpdate videoUpdate);

    public PaginationResponse<List<VideoItemResponse>> getUpdateVideo(Integer page,
            Integer size, String field, SortType sortType);

    public boolean isUpdate(Long videoId);

    public void insertVideoTmpToReal(Long courseTemporary, Course course);

    public List<VideoItemResponse> getVideoTemporaryByCourseTemporaryId(Long courseTemporaryId);

    public void updateVideoOrder(List<VideoOrder> videoOrders, Long courseId);

}
