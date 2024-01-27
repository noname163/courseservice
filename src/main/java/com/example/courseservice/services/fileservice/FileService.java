package com.example.courseservice.services.fileservice;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.response.FileConvertResponse;
import com.example.courseservice.data.dto.response.FileResponse;

public interface FileService {
    public FileResponse fileStorage(MultipartFile file);
    public FileConvertResponse convertFileToFileResponse(MultipartFile videoFile, MultipartFile thumbinal, MultipartFile material);
}
