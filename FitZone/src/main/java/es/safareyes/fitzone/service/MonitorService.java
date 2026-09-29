package es.safareyes.fitzone.service;

import es.safareyes.fitzone.model.Monitor;
import es.safareyes.fitzone.repository.MonitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitorService {

    private final MonitorRepository monitorRepository;

    public List<Monitor> findAll(){
        return monitorRepository.findAll();
    }
}
