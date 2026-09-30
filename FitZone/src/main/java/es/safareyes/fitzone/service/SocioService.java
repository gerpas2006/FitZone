package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.SocioFIltradoDto;
import es.safareyes.fitzone.filter.SocioFilter;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.repository.SocioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocioService {


    private final SocioRepository socioRepository;

    public List<Socio> findAll(){
        return socioRepository.findAll();
    }


    public Page<Socio> filtrar(Pageable pageable, SocioFIltradoDto filtro) {
        Page<Socio> sociosFiltrados = socioRepository.findBy(
                PredicateSpecification.allOf(
                        SocioFilter.filtrarPorEstado(filtro.estado()),
                        SocioFilter.filtarPorPlan(filtro.planId()),
                        SocioFilter.filtrarPorFecha(filtro.fechaInicio(),filtro.fechaFin())
                ), q -> q.page(pageable)
        );
        return sociosFiltrados;
    }

    public Socio findByDni(String dni) {
        return socioRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado el socio con el DNI: " + dni
                ));
    }


    public List<Socio> buscarSocioPorNombre(String nombre){
        List<Socio> listaSocios =  socioRepository.findByNombre(nombre);
        if (listaSocios.isEmpty()){
            throw new EntityNotFoundException("No se han encontrado socios con ese nombre");
        }
        return listaSocios;
    }


}
