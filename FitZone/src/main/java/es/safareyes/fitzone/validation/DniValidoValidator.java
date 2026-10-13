package es.safareyes.fitzone.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DniValidoValidator
        implements ConstraintValidator<DniValido, String> {

    @Override
    public boolean isValid(
            String dni,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        if (dni == null || dni.isBlank()) {
            return true;
        }

        return dni.matches("\\d{8}[A-Za-z]");
    }
}