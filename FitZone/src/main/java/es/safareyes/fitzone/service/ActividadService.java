package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.ActividadResponseDto;
import es.safareyes.fitzone.model.Actividad;
import es.safareyes.fitzone.repository.ActividadRepository;
import es.safareyes.fitzone.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import es.safareyes.fitzone.exception.ElementoNoEncontrado;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final ReservaRepository reservaRepository;


    public List<ActividadResponseDto> actividadesDelDia(
            LocalDate fecha
    ) {
        LocalDateTime inicio = fecha.atStartOfDay();

        LocalDateTime fin = inicio.plusDays(1);

        List<ActividadResponseDto> actividadesDisponibles =
                actividadRepository.findActividadesDelDia(inicio, fin);

        if (actividadesDisponibles.isEmpty()) {
            throw new ElementoNoEncontrado(
                    "No hay actividades libres para ese día"
            );
        }

        return actividadesDisponibles;
    }


}
