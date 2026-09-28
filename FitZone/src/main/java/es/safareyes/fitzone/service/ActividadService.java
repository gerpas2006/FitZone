package es.safareyes.fitzone.service;

import es.safareyes.fitzone.repository.ActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final ActividadRepository actividadRepository;
}
