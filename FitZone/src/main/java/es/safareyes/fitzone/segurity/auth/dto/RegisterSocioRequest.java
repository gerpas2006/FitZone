package es.safareyes.fitzone.segurity.auth.dto;

import es.safareyes.fitzone.validation.DniValido;
import es.safareyes.fitzone.validation.FieldsValueMatch;
import es.safareyes.fitzone.validation.MatriculaValida;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@FieldsValueMatch(
        field = "password",
        fieldMatch = "verifyPassword",
        message = "Los valores de password y verifyPassword no coinciden"
)
public record RegisterSocioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(
                min = 8,
                message = "La contraseña debe tener al menos 8 caracteres"
        )
        String password,

        @NotBlank(message = "Debes confirmar la contraseña")
        String verifyPassword,

        @NotBlank(message = "El DNI es obligatorio")
        @DniValido
        String dni,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono,

        @MatriculaValida
        String matricula,

        @NotNull(message = "El plan es obligatorio")
        UUID planId
) {
}