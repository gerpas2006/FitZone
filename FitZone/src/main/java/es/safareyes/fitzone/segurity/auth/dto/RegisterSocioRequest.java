package es.safareyes.fitzone.segurity.auth.dto;

import es.safareyes.fitzone.validation.DniValido;
import es.safareyes.fitzone.validation.MatriculaValida;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record RegisterSocioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El DNI es obligatorio")
        @DniValido
        String dni,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(
                regexp = "^[0-9]{9}$",
                message = "El teléfono debe tener 9 dígitos"
        )
        String telefono,

        @MatriculaValida
        String matricula,

        @NotNull(message = "El plan es obligatorio")
        UUID planId
) {
}