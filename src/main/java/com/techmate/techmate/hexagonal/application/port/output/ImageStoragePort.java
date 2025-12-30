package com.techmate.techmate.hexagonal.application.port.output;

import java.io.InputStream;

public interface ImageStoragePort {
    String upload(String fileName, InputStream fileStream, String contentType);

    String getPresignedUrl(String fileName);

    void delete(String fileName);
}






