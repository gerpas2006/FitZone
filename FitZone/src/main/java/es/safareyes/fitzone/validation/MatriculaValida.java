package es.safareyes.fitzone.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MatriculaValidaValidator.class)
public @interface MatriculaValida {

    String message() default "La matrícula no tiene un formato válido";

    Class<?>[] groups() default {};

    Class<? extends jakarta.validation.Payload>[] payload() default {};
}