package es.safareyes.fitzone.dto;

public record GenerarCuotasResponse(
        Integer mes,
        Integer anio,
        int cuotasCreadas,
        int sociosInactivos
) {
}
