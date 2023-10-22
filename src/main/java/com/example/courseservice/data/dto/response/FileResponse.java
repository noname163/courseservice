package com.example.courseservice.data.dto.response;

import java.io.InputStream;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileResponse {
    byte[] fileStorage;
    String contentType;
    String fileName;
    Long fileSize;
}
