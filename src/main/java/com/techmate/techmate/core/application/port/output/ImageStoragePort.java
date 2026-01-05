package com.techmate.techmate.core.application.port.output;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStoragePort {
    String saveImage(MultipartFile file, String folderName);

    void deleteImage(String imageUrl);
}
