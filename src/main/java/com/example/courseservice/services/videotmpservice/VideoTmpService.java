package com.example.courseservice.services.videotmpservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.constants.SortType;
import com.example.courseservice.data.dto.request.VideoUpdateRequest;
import com.example.courseservice.data.dto.response.PaginationResponse;
import com.example.courseservice.data.dto.response.VideoAdminResponse;
import com.example.courseservice.data.dto.response.VideoResponse;
import com.example.courseservice.data.entities.VideoTemporary;
import com.example.courseservice.data.object.VideoUpdate;

public interface VideoTmpService {
    public VideoResponse saveTempVideo(VideoUpdateRequest videoUpdateRequest, MultipartFile video,
            MultipartFile thumbnail);

    public void insertVideoUrl(VideoUpdate videoUpdate);

    public PaginationResponse<List<VideoAdminResponse>> getUpdateVideo(Integer page,
            Integer size, String field, SortType sortType);
    public boolean isUpdate(Long videoId);
    public void insertVideoTmpToReal(Long videoId);
}
