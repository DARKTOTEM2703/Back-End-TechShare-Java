package com.techmate.techmate.hexagonal.application.exception;

/**
 * Excepción de dominio para errores del almacenamiento de imágenes.
 */
public class ImageStorageException extends RuntimeException {
    public ImageStorageException(String message) {
        super(message);
    }

    public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
