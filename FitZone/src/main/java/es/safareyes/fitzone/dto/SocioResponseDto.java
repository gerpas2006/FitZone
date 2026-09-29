package es.safareyes.fitzone.dto;

public record SocioResponseDto(
        String nombre,
        String apellidos,
        String email,
        String telefono,
        String direccion,
        String ciudad,
        String provincia,
        String codigoPostal,
        String fechaNacimiento,
        String fechaAlta,
        String fechaBaja,
        String estado
) {
}
