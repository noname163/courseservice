package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileConvertResponse {
    private FileResponse video;
    private FileResponse thumbnail;
    private FileResponse material;
}
