package es.safareyes.fitzone.segurity.auth.dto;

import es.safareyes.fitzone.validation.FieldsValueMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldsValueMatch(
        field = "password",
        fieldMatch = "verifyPassword",
        message = "Los valores de password y verifyPassword no coinciden"
)
public record RegisterRequest(

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

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos
) {
}