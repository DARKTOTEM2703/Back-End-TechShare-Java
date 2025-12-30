package com.techmate.techmate.infrastructure.validation.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.infrastructure.validation.ImageValidationStrategy;

@Component
public class ExtensionValidationStrategy implements ImageValidationStrategy {

    @Override
    public void validate(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es requerido");
        }

        String filename = image.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo es inválido");
        }

        String[] validExtensions = { ".jpg", ".jpeg", ".png", ".gif" };
        boolean isValid = false;
        String lower = filename.toLowerCase();
        for (String extension : validExtensions) {
            if (lower.endsWith(extension)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new IllegalArgumentException(
                    "El archivo debe tener una extensión válida (jpg, jpeg, png, gif).");
        }
    }

}







