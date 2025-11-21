package com.techmate.techmate.imageStorage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageStrategy {
    String saveImage(MultipartFile image);
    void deleteImage(String imagePath);
    public byte[] getImage(String filename);
}


