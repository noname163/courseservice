package com.example.courseservice.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class FileUtils {
    public byte[] compressFile(byte[] data){
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while(!deflater.finished()){
            int size = deflater.deflate(tmp);
            outputStream.write(tmp,0,size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return outputStream.toByteArray();
    }

    public byte[] deCompressFile(byte[] file){
        Inflater inflater = new Inflater();
        inflater.setInput(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(file.length);
        byte[] tmp = new byte[4*1024];
        try {
            while(!inflater.finished()){
                int count = inflater.inflate(tmp);
                outputStream.write(tmp,0,count);
            }
            outputStream.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return outputStream.toByteArray();
    }
}
