package com.example.backend.service.impl;

import com.example.backend.service.UploadFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadFileServiceImpl implements UploadFileService {
    @Override
    public Map<String, String> upload(MultipartFile[] files) {
        System.out.println(files);
        Map<String, String> resp = new HashMap<>();
        for (MultipartFile file : files) {
            System.out.println(file.getName());
            System.out.println(file.getOriginalFilename());
            try {
                System.out.println(new String(file.getBytes(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
