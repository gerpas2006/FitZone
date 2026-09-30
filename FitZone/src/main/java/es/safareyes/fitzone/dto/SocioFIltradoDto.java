package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Estado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SocioFIltradoDto(
        Estado estado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        UUID planId
) {
}
