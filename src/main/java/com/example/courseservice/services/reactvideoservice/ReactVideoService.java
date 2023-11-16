package com.example.courseservice.services.reactvideoservice;

import com.example.courseservice.data.constants.ReactStatus;
import com.example.courseservice.data.dto.request.ReactRequest;

public interface ReactVideoService {
    public void createReact(ReactRequest reactRequest);

    public ReactStatus getReactStatusByStudentIdAndVideoId(Long videoId);
}
