package es.safareyes.fitzone.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented

@Constraint(validatedBy = FieldsValueMatchValidator.class)
public @interface FieldsValueMatch {

    String message() default "Los campos no coinciden";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};

    String field();
    String fieldMatch();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldsValueMatch[] value();
    }

}
