package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Socio;

public record SocioPerfilResponseDto(
        String nombre,
        String apellidos,
        String dni,
        String telefono,
        String matricula,
        PlanResponseDto plan,
        String email
) {

    public static SocioPerfilResponseDto of(Socio socio) {
        return new SocioPerfilResponseDto(
                socio.getUsuario().getNombre(),
                socio.getUsuario().getApellidos(),
                socio.getDni(),
                socio.getTelefono(),
                socio.getMatricula(),
                PlanResponseDto.of(socio.getPlan()),
                socio.getEmail()
        );
    }
}
