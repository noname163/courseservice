package com.example.courseservice.services.UploadService.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.example.courseservice.configs.CloudinaryConfig;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.exceptions.MediaUploadException;
import com.example.courseservice.services.uploadservice.impl.UploadServiceImpl;
import com.example.courseservice.utils.EnvironmentVariable;

class UploadServiceImplTest {

    @Mock
    private CloudinaryConfig cloudinaryConfig;

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private EnvironmentVariable environmentVariable;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private UploadServiceImpl uploadService;

    private Map<String, String> allowContentType = new HashMap<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        allowContentType.put("image/jpeg", "jpg");
        allowContentType.put("image/png", "png");
        allowContentType.put("application/pdf", "pdf");
        allowContentType.put("image/gif", "gif");
        allowContentType.put("video/mp4", "mp4");
    }

    @Test
    void uploadMedia_ValidFile_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        when(environmentVariable.initializeAllowedContentTypes()).thenReturn(allowContentType);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(Mockito.any(byte[].class), Mockito.any()))
                .thenReturn(createCloudinaryResponse("test_public_id"));

        String publicId = uploadService.uploadMedia(file);

        assertEquals("test_public_id", publicId);
    }

    @Test
    void uploadMedia_InvalidContentType_ThrowsBadRequestException() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());

        when(environmentVariable.initializeAllowedContentTypes()).thenReturn(allowContentType);

        BadRequestException result = assertThrows(BadRequestException.class, () -> uploadService.uploadMedia(file));

        assertEquals("Unsupported file type. Supported types are: png, jpg, pdf, gif, mp4", result.getMessage());

    }

    @Test
    void uploadMedia_ExceptionOccurred_ThrowsMediaUploadException() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        
        when(environmentVariable.initializeAllowedContentTypes()).thenReturn(allowContentType);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(cloudinary.uploader().upload(Mockito.any(byte[].class), Mockito.any())).thenThrow(new IOException());

        try {
            uploadService.uploadMedia(file);
        } catch (MediaUploadException e) {
            assertEquals("Failed to upload media", e.getMessage());
        }
    }

    @Test
    void uploadMediaList_ValidFiles_Success() throws IOException {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.jpg", "image/jpeg", "test data".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.jpg", "image/jpeg", "test data".getBytes());
        
        when(cloudinary.uploader()).thenReturn(uploader);
        when(environmentVariable.initializeAllowedContentTypes()).thenReturn(allowContentType);
        when(cloudinary.uploader().upload(Mockito.any(byte[].class), Mockito.any()))
                .thenReturn(createCloudinaryResponse("public_id_1"));

        List<String> publicIds = uploadService.uploadMediaList(Arrays.asList(file1, file2));

        assertEquals(2, publicIds.size());
        assertEquals("public_id_1", publicIds.get(0));
    }

    private Map<String, Object> createCloudinaryResponse(String publicId) {
        Map<String, Object> response = new HashMap<>();
        response.put("public_id", publicId);
        return response;
    }
}
