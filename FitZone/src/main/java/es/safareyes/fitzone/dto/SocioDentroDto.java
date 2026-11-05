package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Acceso;
import es.safareyes.fitzone.model.Socio;

import java.util.List;

public record SocioDentroDto(
        String nombre,
        String apellidos,
        String dni
) {
    public static SocioDentroDto of(Socio socio) {
        return new SocioDentroDto(
                socio.getUsuario().getNombre(),
                socio.getUsuario().getApellidos(),
                socio.getDni()
        );
    }
}