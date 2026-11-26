package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.SocioEditarDto;
import es.safareyes.fitzone.dto.SocioFIltradoDto;
import es.safareyes.fitzone.dto.SocioMorosoResponseDto;
import es.safareyes.fitzone.filter.SocioFilter;
import es.safareyes.fitzone.model.*;
import es.safareyes.fitzone.repository.CuotaRepository;
import es.safareyes.fitzone.repository.PlanRepository;
import es.safareyes.fitzone.repository.SocioRepository;
import es.safareyes.fitzone.exception.ElementoNoEncontrado;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
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
        List<Socio> lista = socioRepository.findAll();
        if (lista.isEmpty()){
            throw new ElementoNoEncontrado("No hay socios registrados");
        }
        return lista;
    }


    public Page<Socio> filtrar(Pageable pageable, SocioFIltradoDto filtro) {
        // Combina los filtros recibidos y pagina los resultados.
        Page<Socio> sociosFiltrados = socioRepository.findBy(
                PredicateSpecification.allOf(
                        SocioFilter.filtrarPorEstado(filtro.estado()),
                        SocioFilter.filtrarPorDni(filtro.dni()),
                        SocioFilter.filtarPorPlan(filtro.planId()),
                        SocioFilter.filtrarPorNombre(filtro.nombre()),
                        SocioFilter.filtrarPorFecha(filtro.fechaInicio(),filtro.fechaFin())
                ),q -> q.project("plan", "usuario").page(pageable)

        );
        return sociosFiltrados;
    }

    public Socio findByDni(String dni) {
        return socioRepository.findByDni(dni)
                .orElseThrow(() -> new ElementoNoEncontrado(
                        "No se ha encontrado el socio con el DNI: " + dni
                ));
    }


    public List<Socio> buscarSocioPorNombre(String nombre){
        List<Socio> listaSocios = socioRepository.findByUsuario_NombreContainingIgnoreCase(nombre);
        if (listaSocios.isEmpty()){
            throw new ElementoNoEncontrado("No se han encontrado socios con ese nombre");
        }
        return listaSocios;
    }

    public Socio buscarFichaSocio(UUID id){
        return socioRepository.findWithPlanAndUsuarioById(id).orElseThrow(() ->
                new ElementoNoEncontrado("No se ha encontrado ninguna ficha del socio con ese id"));
    }

    public Socio buscarPerfilPorUsuario(UUID usuarioId) {
        return socioRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ElementoNoEncontrado(
                        "No se ha encontrado la ficha del socio asociado a este usuario"
                ));
    }

    @Transactional
    public Socio editarSocio(UUID id, SocioEditarDto datosSocio) {
        Socio socio = socioRepository.findWithPlanAndUsuarioById(id)
                .orElseThrow(() -> new ElementoNoEncontrado(
                        "No se ha encontrado ningún socio con ese id"
                ));

        if (datosSocio.email() != null) {
            if (socioRepository.existsByEmailIgnoreCaseAndIdNot(datosSocio.email(), id)) {
                // El email debe ser único entre los demás socios.
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
            }
            socio.setEmail(datosSocio.email());
        }

        if (datosSocio.telefono() != null) {
            if (socioRepository.existsByTelefonoAndIdNot(datosSocio.telefono(), id)) {
                // El teléfono tampoco puede pertenecer a otro socio.
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El teléfono ya está registrado");
            }
            socio.setTelefono(datosSocio.telefono());
        }

        if (datosSocio.matricula() != null) {
            String matricula = datosSocio.matricula().isBlank()
                    ? null
                    : datosSocio.matricula().trim().toUpperCase();
            if (matricula != null && socioRepository.existsByMatriculaAndIdNot(matricula, id)) {
                // Comprueba duplicados solo si se está asignando una matrícula.
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La matrícula ya está registrada");
            }
            socio.setMatricula(matricula);
        }

        if (datosSocio.planId() != null) {
            Plan plan = planRepository.findById(datosSocio.planId())
                    .orElseThrow(() -> new ElementoNoEncontrado(
                            "No se ha encontrado ningún plan con ese id"
                    ));

            socio.setPlan(plan);
        }

        return socioRepository.save(socio);
    }

    @Transactional
    public Socio activarDesactivarSocio(UUID id) {
        Socio socio = socioRepository.findWithPlanAndUsuarioById(id)
                .orElseThrow(() ->
                        new ElementoNoEncontrado(
                                "No se ha encontrado el socio con ese id"
                        ));

        if (socio.getEstado().equals(Estado.ACTIVO)) {
            socio.setEstado(Estado.INACTIVO);
        } else {
            socio.setEstado(Estado.ACTIVO);
        }

        return socioRepository.save(socio);
    }

    public void comprobarAccesoPropio(Usuario usuario, UUID socioId) {
        if (usuario == null || usuario.getRol() != Rol.SOCIO) {
            return;
        }

        // Un socio solo puede consultar su propia ficha.
        boolean esPropio = socioRepository.findByUsuario_Id(usuario.getId())
                .map(socio -> socio.getId().equals(socioId))
                .orElse(false);

        if (!esPropio) {
            // Bloquea el acceso a fichas de otros socios.
            throw new AccessDeniedException("Solo puedes consultar tus propios datos");
        }
    }

    public Page<Cuota> obtenerHistorialPorSocio(
            UUID socioId,
            Pageable pageable
    ) {
        if (!socioRepository.existsById(socioId)) {
            throw new ElementoNoEncontrado("No se ha encontrado el socio con ese id");
        }

        return cuotaRepository.findBySocio_Id(socioId, pageable);
    }

    public List<SocioMorosoResponseDto> obtenerSociosMorosos(
            Integer mes,
            Integer anio
    ) {
        List<SocioMorosoResponseDto> listaMorosos = socioRepository
                .findSociosConCuotaPendiente(mes, anio)
                .stream()
                .map(SocioMorosoResponseDto::of)
                .toList();

        if (listaMorosos.isEmpty()) {
            throw new ElementoNoEncontrado(
                    "Muy bien, no hay morosos a la vista"
            );
        }

        return listaMorosos;
    }


}
