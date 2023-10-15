package com.example.courseservice.services.fileservice;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public List<MultipartFile> splitFile(MultipartFile inputMultipartFile) throws IOException;
}
