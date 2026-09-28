package es.safareyes.fitzone.service;

import es.safareyes.fitzone.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocioService {

    private final SocioRepository socioRepository;
}
