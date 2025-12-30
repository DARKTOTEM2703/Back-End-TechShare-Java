package com.techmate.techmate.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Implementación del validator para @SafeString.
 * Previene XSS, SQL injection, y patrones maliciosos.
 */
public class SafeStringValidator implements ConstraintValidator<SafeString, String> {
    
    // Patrones XSS comunes
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|javascript:|onerror=|onclick=|onload=|<iframe|<object|<embed|" +
        "eval\\(|expression\\(|vbscript:|<svg|<img|<video|<audio|<form|<input|" +
        "expression\\s*\\(|behavior\\s*:|@import|<meta|<link|<style|on\\w+\\s*=)"
    );
    
    // Patrones SQL injection comunes
    private static final Pattern SQL_PATTERN = Pattern.compile(
        "(?i)(\\bunion\\b|\\bselect\\b|\\bwhere\\b|\\binsert\\b|\\bupdate\\b|\\bdelete\\b|" +
        "\\bdrop\\b|\\btruncate\\b|\\bcreate\\b|\\balter\\b|\\bexec\\b|\\bexecute\\b|" +
        "--|;\\s*drop|;\\s*delete|;\\s*insert|;\\s*update|'\\s*or\\s*'|\"\\s*or\\s*\")"
    );
    
    // Caracteres de control sospechosos
    private static final Pattern CONTROL_CHARS = Pattern.compile(
        "[\\x00-\\x08\\x0B-\\x0C\\x0E-\\x1F\\x7F]"
    );
    
    // Patrones específicos según allowSpecial
    private static final Pattern ALPHANUMERIC_ONLY = Pattern.compile("^[a-zA-Z0-9_\\-.]+$");
    
    private boolean allowSpecial;

    @Override
    public void initialize(SafeString annotation) {
        this.allowSpecial = annotation.allowSpecial();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null es permitido (usar @NotNull/NotBlank si es requerido)
        if (value == null) {
            return true;
        }

        // String vacío es válido
        if (value.trim().isEmpty()) {
            return true;
        }

        // Validar límites razonables de longitud (evitar DoS)
        if (value.length() > 10000) {
            addConstraintViolation(context, "El campo excede la longitud máxima permitida (10000 caracteres)");
            return false;
        }

        // Detectar XSS patterns
        if (XSS_PATTERN.matcher(value).find()) {
            addConstraintViolation(context, "Detectado patrón XSS potencial");
            return false;
        }

        // Detectar SQL injection patterns
        if (SQL_PATTERN.matcher(value).find()) {
            addConstraintViolation(context, "Detectado patrón SQL injection potencial");
            return false;
        }

        // Detectar caracteres de control
        if (CONTROL_CHARS.matcher(value).find()) {
            addConstraintViolation(context, "Detectados caracteres de control no permitidos");
            return false;
        }

        // Limitar newlines (máximo 5 para comentarios multi-línea)
        long newlineCount = value.chars().filter(c -> c == '\n').count();
        if (newlineCount > 5) {
            addConstraintViolation(context, "Demasiadas líneas en el campo (máx 5)");
            return false;
        }

        // Si no permite especiales, validar solo alfanuméricos
        if (!allowSpecial && !ALPHANUMERIC_ONLY.matcher(value).matches()) {
            addConstraintViolation(context, "El campo contiene caracteres especiales no permitidos");
            return false;
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}






