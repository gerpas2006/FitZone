package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Actividad;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActividadResponseDto(
        UUID id,
        String nombre,
        LocalDateTime fechaHora,
        Integer duracionMinutos,
        String sala,
        Integer aforoMaximo,
        long reservasActuales,
        long plazasDisponibles,
        UUID monitorId
) {

    public static ActividadResponseDto of(
            Actividad actividad,
            long reservasActuales
    ) {
        long plazasDisponibles =
                actividad.getAforoMaximo() - reservasActuales;

        return new ActividadResponseDto(
                actividad.getId(),
                actividad.getNombre(),
                actividad.getFechaHora(),
                actividad.getDuracionMinutos(),
                actividad.getSala(),
                actividad.getAforoMaximo(),
                reservasActuales,
                plazasDisponibles,
                actividad.getMonitor() != null
                        ? actividad.getMonitor().getId()
                        : null
        );
    }
}