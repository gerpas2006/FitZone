package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Actividad;
import es.safareyes.fitzone.model.Reserva;
import es.safareyes.fitzone.model.Socio;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CrearReservaRequest(

        @NotNull(message = "La actividad es obligatoria")
        UUID actividadId

) {

    public static Reserva toEntity(
            CrearReservaRequest request,
            Socio socio,
            Actividad actividad
    ) {
        return Reserva.builder()
                .fechaReserva(LocalDateTime.now())
                .socio(socio)
                .actividad(actividad)
                .build();
    }
}