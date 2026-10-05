package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Cuota;
import es.safareyes.fitzone.model.EstadoCuota;

import java.time.LocalDateTime;
import java.util.UUID;

public record CuotaResponseDto(
        UUID id,
        Integer mes,
        Integer anio,
        double importe,
        EstadoCuota estadoCuota,
        LocalDateTime fechaPago,
        UUID socioId,
        String dniSocio,
        UUID planId
) {
    public static CuotaResponseDto of(Cuota cuota) {
        return new CuotaResponseDto(
                cuota.getId(),
                cuota.getMes(),
                cuota.getAnio(),
                cuota.getImporte(),
                cuota.getEstadoCuota(),
                cuota.getFechaPago(),
                cuota.getSocio().getId(),
                cuota.getSocio().getDni(),
                cuota.getPlan().getId()
        );
    }
}