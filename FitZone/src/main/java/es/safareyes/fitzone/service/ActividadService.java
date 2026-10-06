package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.ActividadResponseDto;
import es.safareyes.fitzone.model.Actividad;
import es.safareyes.fitzone.repository.ActividadRepository;
import es.safareyes.fitzone.repository.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            LocalDateTime fechaHora
    ) {
        LocalDateTime inicio;
        LocalDateTime fin;
        List<ActividadResponseDto> actividadesDisponibles;

        inicio = fechaHora.withMinute(0).withSecond(0).withNano(0);
        fin = inicio.plusHours(1);

        actividadesDisponibles =
                actividadRepository.findActividadesDelDia(inicio, fin);

        if (actividadesDisponibles.isEmpty()) {
            throw new EntityNotFoundException(
                    "No hay actividades libres para esa hora"
            );
        }

        return actividadesDisponibles;
    }


}
