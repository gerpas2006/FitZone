package es.safareyes.fitzone.service;

import es.safareyes.fitzone.model.Actividad;
import es.safareyes.fitzone.repository.ActividadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final ActividadRepository actividadRepository;

    public List<Actividad> actividadesDelDia(LocalDateTime fechaHora){
        List<Actividad> listaBuscada = actividadRepository.findActividadesDelDia(fechaHora);
        if (listaBuscada.isEmpty()){
            throw new EntityNotFoundException("No hay actividades con la fecha seleccionada");
        }
        return listaBuscada;
    }



}
