package com.example.courseservice.services.fileservice.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.data.dto.response.FileResponse;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.fileservice.FileService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class FileServiceImpl implements FileService {
    @Override
    public FileResponse fileStorage(MultipartFile file) {
        if (file==null) {
            throw new BadRequestException("Data invalid.");
        }
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            byte[] fileData;
            try {
                fileData = file.getBytes();
                return FileResponse
                        .builder()
                        .contentType(file.getContentType())
                        .fileName(fileName)
                        .fileSize(file.getSize())
                        .fileStorage(fileData)
                        .build();
            } catch (IOException e) {
                log.error("Error in convert file {}",e.getMessage());
            }

        }
        return null;
    }

}
