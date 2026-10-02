package es.safareyes.fitzone.dto;

import jakarta.validation.constraints.Email;

import java.util.UUID;

public record SocioEditarDto(
        @Email
        String email,
        String telefono,
        String matricula,
        UUID planId
) {
}
