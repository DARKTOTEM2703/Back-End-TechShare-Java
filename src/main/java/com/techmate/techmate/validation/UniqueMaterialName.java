package com.techmate.techmate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Anotación para validar que el nombre de un material sea único.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueMaterialNameValidator.class)
@Documented
public @interface UniqueMaterialName {
    String message() default "El nombre del material ya existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

