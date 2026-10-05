package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.Socio;

import java.time.LocalDate;
import java.util.UUID;

public record SocioResponseDto(
        UUID id,
        String dni,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        LocalDate fechaAlta,
        Estado estado,
        UUID planId
) {

    public static SocioResponseDto of(Socio socio) {
        return new SocioResponseDto(
                socio.getId(),
                socio.getDni(),
                socio.getNombre(),
                socio.getApellidos(),
                socio.getEmail(),
                socio.getTelefono(),
                socio.getFechaAlta(),
                socio.getEstado(),
                socio.getPlan().getId()
        );
    }
}
