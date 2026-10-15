package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Rol;
import es.safareyes.fitzone.model.Usuario;

import java.util.UUID;

public record UsuarioResponseDto(
        UUID id,
        String nombre,
        String apellidos,
        String username,
        Rol rol,
        UUID socioId
) {

    public static UsuarioResponseDto of(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getUsername(),
                usuario.getRol(),
                usuario.getSocio() != null
                        ? usuario.getSocio().getId()
                        : null
        );
    }
}