package com.techmate.techmate.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation para validar strings contra XSS/SQL injection patterns.
 * 
 * Uso:
 * @SafeString
 * private String comment;
 * 
 * Rechaza:
 * - Scripts: <script>, javascript:, onerror=, etc.
 * - SQL: UNION, DROP, DELETE, INSERT, UPDATE, WHERE
 * - Control chars: newlines excesivas, null bytes
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SafeStringValidator.class)
@Documented
public @interface SafeString {
    
    String message() default "El campo contiene caracteres o patrones no permitidos (XSS/Injection)";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Si es true, permite espacios, puntuación normal, acentos.
     * Si es false, solo alfanuméricos e _ - .
     */
    boolean allowSpecial() default true;
}






