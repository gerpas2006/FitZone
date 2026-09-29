package es.safareyes.fitzone.service;

import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocioService {

    private final SocioRepository socioRepository;

    public List<Socio> findAll(){
        return socioRepository.findAll();
    }



}
