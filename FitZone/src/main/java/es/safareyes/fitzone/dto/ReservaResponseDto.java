package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Reserva;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaResponseDto(
        UUID id,
        LocalDateTime fechaReserva,
        UUID socioId,
        UUID actividadId,
        String actividadNombre,
        LocalDateTime actividadFechaHora
) {

    public static ReservaResponseDto of(Reserva reserva) {
        return new ReservaResponseDto(
                reserva.getId(),
                reserva.getFechaReserva(),
                reserva.getSocio().getId(),
                reserva.getActividad().getId(),
                reserva.getActividad().getNombre(),
                reserva.getActividad().getFechaHora()
        );
    }
}