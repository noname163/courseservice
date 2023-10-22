package com.example.courseservice.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

public class InputStreamMultipartFile implements MultipartFile {

        private final InputStream inputStream;
        private final String name;

        public InputStreamMultipartFile(InputStream inputStream, String name) {
            this.inputStream = inputStream;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return name;
        }

        @Override
        public String getContentType() {
            return "video/mp4";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            try {
                return inputStream.available();
            } catch (IOException e) {
                return 0;
            }
        }

        @Override
        public byte[] getBytes() throws IOException {
            return StreamUtils.copyToByteArray(inputStream);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return inputStream;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (OutputStream os = new FileOutputStream(dest)) {
                StreamUtils.copy(inputStream, os);
            }
        }
 
}
