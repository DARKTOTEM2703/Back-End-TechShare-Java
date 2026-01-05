package com.techmate.techmate.core.application.port.input;

import java.io.InputStream;

public interface ImageUploadUseCase {
    String uploadImage(String fileName, InputStream fileStream, String contentType);

    String getImageUrl(String imageKey);

    void deleteImage(String imageKey);
}






