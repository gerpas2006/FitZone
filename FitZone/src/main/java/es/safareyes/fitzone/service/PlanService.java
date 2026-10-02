package es.safareyes.fitzone.service;

import es.safareyes.fitzone.model.Plan;
import es.safareyes.fitzone.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    public List<Plan> listarPlanes(){
        List<Plan> listaPlanes = planRepository.findAll();
        if (listaPlanes.isEmpty()){
            new Exception("No hay ningun plan registrado en este momento");
        }
        return  listaPlanes;
    }


}
