package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.validation.MatriculaValida;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record SocioEditarDto(

        @Email(message = "El email no tiene un formato válido")
        String email,

        @Pattern(
                regexp = "^[0-9]{9}$",
                message = "El teléfono debe tener 9 dígitos"
        )
        String telefono,

        @MatriculaValida(
                message = "La matrícula no tiene un formato válido"
        )
        String matricula,

        UUID planId
) {
}