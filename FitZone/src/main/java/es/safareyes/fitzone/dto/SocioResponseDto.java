package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.model.Usuario;

import java.time.LocalDate;
import java.util.UUID;

public record SocioResponseDto(
        UUID id,
        String dni,
        String nombre,
        String apellidos,
        String email,
        String matricula,
        String telefono,
        LocalDate fechaAlta,
        Estado estado,
        UUID planId,
        String planNombre
) {

    public static SocioResponseDto of(Socio socio) {
        Usuario usuario = socio.getUsuario();

        String nombre = usuario != null ? usuario.getNombre() : null;
        String apellidos = usuario != null ? usuario.getApellidos() : null;

        return new SocioResponseDto(
                socio.getId(),
                socio.getDni(),
                nombre,
                apellidos,
                socio.getEmail(),
                socio.getMatricula(),
                socio.getTelefono(),
                socio.getFechaAlta(),
                socio.getEstado(),
                socio.getPlan().getId(),
                socio.getPlan().getNombre()
        );
    }
}
