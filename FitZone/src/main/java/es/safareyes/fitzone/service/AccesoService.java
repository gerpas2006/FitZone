package es.safareyes.fitzone.service;

import es.safareyes.fitzone.repository.AccesoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final AccesoRepository accesoRepository;
}
