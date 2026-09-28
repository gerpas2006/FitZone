package es.safareyes.fitzone.service;

import es.safareyes.fitzone.repository.CuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuotaService {

    private final CuotaRepository cuotaRepository;
}
