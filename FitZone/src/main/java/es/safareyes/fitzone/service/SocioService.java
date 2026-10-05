package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.SocioEditarDto;
import es.safareyes.fitzone.dto.SocioFIltradoDto;
import es.safareyes.fitzone.filter.SocioFilter;
import es.safareyes.fitzone.model.*;
import es.safareyes.fitzone.repository.CuotaRepository;
import es.safareyes.fitzone.repository.PlanRepository;
import es.safareyes.fitzone.repository.SocioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocioService {


    private final SocioRepository socioRepository;
    private final PlanRepository planRepository;
    private final CuotaRepository cuotaRepository;

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

    public Socio buscarFichaSocio(UUID id){
        return socioRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("No se ha encontrado ninguna ficha del socio con ese id"));
    }

    public Socio editarSocio(UUID id, SocioEditarDto datosSocio) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado ningún socio con ese id"
                ));

        if (datosSocio.email() != null) {
            socio.setEmail(datosSocio.email());
        }

        if (datosSocio.telefono() != null) {
            socio.setTelefono(datosSocio.telefono());
        }

        if (datosSocio.matricula() != null) {
            socio.setMatricula(datosSocio.matricula());
        }

        if (datosSocio.planId() != null) {
            Plan plan = planRepository.findById(datosSocio.planId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "No se ha encontrado ningún plan con ese id"
                    ));

            socio.setPlan(plan);
        }

        return socioRepository.save(socio);
    }

    public Socio activarDesactivarSocio(UUID id){
        Socio socioBuscado = socioRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException("No se ha encontrado el socio con ese id"));
        if (socioBuscado.getEstado().equals(Estado.ACTIVO)){
            socioBuscado.setEstado(Estado.INACTIVO);
        }else {
            socioBuscado.setEstado(Estado.ACTIVO);
        }
        return socioRepository.save(socioBuscado);
    }

    public Page<Cuota> obtenerHistorialPorSocio(
            UUID socioId,
            Pageable pageable
    ) {
        return cuotaRepository.findBySocio_Id(socioId, pageable);
    }

    public List<Socio> obtenerSociosMorosos(Integer mes, Integer anio) {
        List<Socio> listaMorosos = socioRepository.findSociosConCuotaPendiente(mes, anio);
        if (listaMorosos.isEmpty()){
            throw new EntityNotFoundException("Muy bien no hay morosos a la vista");
        }
        return listaMorosos;
    }


}
