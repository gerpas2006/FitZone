package es.safareyes.fitzone.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MatriculaValidaValidator
        implements ConstraintValidator<MatriculaValida, String> {
    private static final String LETRAS_MATRICULA =
            "BCDFGHJKLMNPRSTVWXYZ";

    @Override
    public boolean isValid(
            String matricula,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        if (matricula == null || matricula.isBlank()) {
            return true;
        }

        if (!matricula.matches("\\d{4}[BCDFGHJKLMNPRSTVWXYZ]{3}")) {
            return false;
        }

        return matricula.substring(4)
                .chars()
                .allMatch(letra -> LETRAS_MATRICULA.indexOf(letra) >= 0);
    }
}