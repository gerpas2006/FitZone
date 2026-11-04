package es.safareyes.fitzone.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DniValidoValidator
        implements ConstraintValidator<DniValido, String> {

    private static final String LETRAS_DNI =
            "TRWAGMYFPDXBNJZSQVHLCKE";

    @Override
    public boolean isValid(
            String dni,
            ConstraintValidatorContext context
    ) {
        if (dni == null || dni.isBlank()) {
            return true;
        }

        if (!dni.matches("\\d{8}[A-Za-z]")) {
            return false;
        }

        int numero = Integer.parseInt(dni.substring(0, 8));
        char letraEsperada = LETRAS_DNI.charAt(numero % 23);

        return Character.toUpperCase(dni.charAt(8)) == letraEsperada;
    }
}