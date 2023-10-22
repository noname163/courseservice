// package com.example.courseservice.services.UploadService.impl;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;

// import java.io.IOException;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.springframework.mock.web.MockMultipartFile;

// import com.cloudinary.Cloudinary;
// import com.cloudinary.Uploader;
// import com.example.courseservice.configs.CloudinaryConfig;
// import com.example.courseservice.data.dto.response.CloudinaryUrl;
// import com.example.courseservice.data.dto.response.FileResponse;
// import com.example.courseservice.exceptions.BadRequestException;
// import com.example.courseservice.exceptions.MediaUploadException;
// import com.example.courseservice.services.uploadservice.impl.UploadServiceImpl;
// import com.example.courseservice.utils.EnvironmentVariable;

// class UploadServiceImplTest {

//     @Mock
//     private CloudinaryConfig cloudinaryConfig;

//     @Mock
//     private Cloudinary cloudinary;

//     @Mock
//     private EnvironmentVariable environmentVariable;

//     @Mock
//     private Uploader uploader;

//     @InjectMocks
//     private UploadServiceImpl uploadService;

//     private Map<String, String> allowContentType = new HashMap<>();

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//         allowContentType.put("image/jpeg", "jpg");
//         allowContentType.put("image/png", "png");
//         allowContentType.put("application/pdf", "pdf");
//         allowContentType.put("image/gif", "gif");
//         allowContentType.put("video/mp4", "mp4");
//     }

//     @Test
//     void uploadMedia_ValidFile_Success() throws IOException {
//         MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

//         when(environmentVariable.initializeAllowedContentTypes()).thenReturn(allowContentType);
//         when(cloudinary.uploader()).thenReturn(uploader);
//         when(uploader.upload(Mockito.any(byte[].class), Mockito.any()))
//                 .thenReturn(createCloudinaryResponse("test_public_id"));

//         // String publicId = uploadService.uploadMedia(file);

//         // assertEquals("test_public_id", publicId);
//     }

//     @Test
//     void testUploadMedia_Success() throws IOException {
//         // Mock environmentVariable to return a content type
//         when(environmentVariable.initializeAllowedContentTypes()).thenReturn(allowedContentTypes());

//         // Mock cloudinary uploader behavior
        

//         // Create a sample FileResponse
//         FileResponse fileResponse = FileResponse.builder().build();
//         fileResponse.setContentType("image/jpeg");

//         Map<String, String> options = new HashMap<>();
//         options.put("resource_type", "auto");
//         when(cloudinary.uploader()).thenReturn(uploader);
//         when(uploader.upload(fileResponse.getFileStorage(), options))
//                 .thenReturn(cloudinaryUploadResult());
//         // Perform the uploadMedia operation
//         CloudinaryUrl cloudinaryUrl = uploadService.uploadMedia(fileResponse);

//         // Verify that the result is not null
//         assertNotNull(cloudinaryUrl);
//         // Add assertions based on the behavior of cloudinaryUploader and the expected CloudinaryUrl
//     }

//     void testUploadMedia_UnsupportedFileType() {
//         // Mock environmentVariable to return a content type
//         Mockito.when(environmentVariable.initializeAllowedContentTypes()).thenReturn(allowedContentTypes());

//         // Create a sample FileResponse with an unsupported content type
//         FileResponse fileResponse = FileResponse.builder().build();
//         fileResponse.setContentType("application/pdf");

//         // Perform the uploadMedia operation, which should throw a BadRequestException
//         uploadService.uploadMedia(fileResponse);
//     }

//     void testUploadMedia_IOException() throws IOException {
//         // Mock environmentVariable to return a content type
//         Mockito.when(environmentVariable.initializeAllowedContentTypes()).thenReturn(allowedContentTypes());

//         // Mock cloudinary uploader behavior to throw an IOException
//         Mockito.when(cloudinary.uploader().upload(Mockito.any(), Mockito.any()))
//                 .thenThrow(new IOException("Upload failed"));

//         // Create a sample FileResponse
//         FileResponse fileResponse = FileResponse.builder().build();
//         fileResponse.setContentType("image/jpeg");

//         // Perform the uploadMedia operation, which should throw a MediaUploadException
//         uploadService.uploadMedia(fileResponse);
//     }

//     // Define the behavior of the environment variable regarding allowed content
//     // types
//     private Map<String, String> allowedContentTypes() {
//         Map<String, String> contentTypes = new HashMap<>();
//         contentTypes.put("image/jpeg", "JPEG");
//         contentTypes.put("image/png", "PNG");
//         return contentTypes;
//     }

//     // Define a sample Cloudinary upload result
//     private Map<String, Object> cloudinaryUploadResult() {
//         Map<String, Object> uploadResult = new HashMap<>();
//         uploadResult.put("public_id", "sample_public_id");
//         uploadResult.put("duration", "10.5");
//         return uploadResult;
//     }

//     private Map<String, Object> createCloudinaryResponse(String publicId) {
//         Map<String, Object> response = new HashMap<>();
//         response.put("public_id", publicId);
//         return response;
//     }
// }
