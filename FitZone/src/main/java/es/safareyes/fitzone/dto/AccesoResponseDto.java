package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Acceso;
import es.safareyes.fitzone.model.Cuota;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccesoResponseDto(
        UUID id,
        LocalDateTime fechaHoraEntrada,
        LocalDateTime fechaHoraSalida,
        boolean resultado,
        String motivoRechazo,
        boolean bonificacionParkingAplicada,
        UUID socioId
) {
    public static AccesoResponseDto of(Acceso acceso) {
        return new AccesoResponseDto(
                acceso.getId(),
                acceso.getFechaHoraEntrada(),
                acceso.getFechaHoraSalida(),
                acceso.isResultado(),
                acceso.getMotivoRechazo(),
                acceso.isBonificacionParkingAplicada(),
                acceso.getId()
        );
    }
}
