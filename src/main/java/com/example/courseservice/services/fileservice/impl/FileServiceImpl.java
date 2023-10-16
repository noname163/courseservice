package com.example.courseservice.services.fileservice.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseservice.services.fileservice.FileService;
import com.example.courseservice.utils.InputStreamMultipartFile;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class FileServiceImpl implements FileService {

    @Override
    public List<MultipartFile> splitFile(MultipartFile inputMultipartFile) throws IOException {
        long maxSegmentSize = 1 * 1024 * 1024;

        File tempDir = Files.createTempDirectory("video_segments").toFile();

        File inputFile = new File(tempDir, "input_video.mp4");
        inputMultipartFile.transferTo(inputFile);

        List<MultipartFile> videoSegments = new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", inputFile.getAbsolutePath(),
                    "-fs", Long.toString(maxSegmentSize),
                    "-f", "segment",
                    "-segment_time", "2",
                    "-reset_timestamps", "1",
                    tempDir.getAbsolutePath() + File.separator + "output%d.mp4");
            Process process = processBuilder.start();
            process.waitFor();

            File[] segmentFiles = tempDir.listFiles((dir, name) -> name.startsWith("output"));

            if (segmentFiles != null) {
                for (File segmentFile : segmentFiles) {
                    FileInputStream fileInputStream = new FileInputStream(segmentFile);
                    MultipartFile multipartFile = new InputStreamMultipartFile(fileInputStream, segmentFile.getName());
                    videoSegments.add(multipartFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (File segmentFile : tempDir.listFiles()) {
                segmentFile.delete();
            }
            tempDir.delete();
        }

        return videoSegments;
    }

}
