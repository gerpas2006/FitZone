package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.AccesoResponseDto;
import es.safareyes.fitzone.dto.AforoResponseDto;
import es.safareyes.fitzone.dto.SocioDentroDto;
import es.safareyes.fitzone.model.Acceso;
import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.EstadoCuota;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.repository.AccesoRepository;
import es.safareyes.fitzone.repository.SocioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final AccesoRepository accesoRepository;
    private final SocioRepository socioRepository;

    public Acceso tornoEntrada(String dni) {
        Socio socioBuscado = socioRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado el socio con ese DNI"
                ));

        Acceso nuevoAcceso = new Acceso();
        nuevoAcceso.setSocio(socioBuscado);
        nuevoAcceso.setFechaHoraEntrada(LocalDateTime.now());
        nuevoAcceso.setFechaHoraSalida(null);
        nuevoAcceso.setResultado(false);

        if (socioBuscado.getEstado() != Estado.ACTIVO) {
            nuevoAcceso.setMotivoRechazo("El socio está inactivo");
            return accesoRepository.save(nuevoAcceso);
        }

        if (socioBuscado.getCuotas().equals(EstadoCuota.PENDIENTE)) {
            nuevoAcceso.setMotivoRechazo("El socio tiene cuotas pendientes");
            return accesoRepository.save(nuevoAcceso);
        }

        LocalDate hoy = LocalDate.now();

        long aforoActual = accesoRepository.countEntradasAbiertasDelDia(
                hoy.atStartOfDay(),
                hoy.plusDays(1).atStartOfDay()
        );

        if (aforoActual >= 150) {
            nuevoAcceso.setMotivoRechazo("El aforo está completo");
            return accesoRepository.save(nuevoAcceso);
        }

        if (accesoRepository.existsBySocioAndResultadoTrueAndFechaHoraSalidaIsNull(socioBuscado)) {
            nuevoAcceso.setMotivoRechazo("El socio ya se encuentra dentro del gimnasio");
            return accesoRepository.save(nuevoAcceso);
        }
        nuevoAcceso.setResultado(true);
        nuevoAcceso.setMotivoRechazo(null);

        return accesoRepository.save(nuevoAcceso);
    }

    @Transactional
    public AccesoResponseDto tornoSalida(String dni) {
        Socio socioBuscado = socioRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado el socio con ese DNI"
                ));

        Acceso accesoExistente = accesoRepository
                .findAccesoAbiertoBySocio(socioBuscado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "El socio no tiene una entrada abierta"
                ));

        accesoExistente.setFechaHoraSalida(LocalDateTime.now());

        Acceso accesoGuardado = accesoRepository.save(accesoExistente);

        return AccesoResponseDto.of(accesoGuardado);
    }
    public AforoResponseDto obtenerAforo() {
        LocalDate hoy = LocalDate.now();

        long personasDentro = accesoRepository.countEntradasAbiertasDelDia(
                hoy.atStartOfDay(),
                hoy.plusDays(1).atStartOfDay()
        );

        return new AforoResponseDto(
                personasDentro,
                150
        );
    }

    public List<Acceso> obtenerSociosDentro() {
        LocalDate hoy = LocalDate.now();

        return accesoRepository.findSociosDentro(
                hoy.atStartOfDay(),
                hoy.plusDays(1).atStartOfDay()
        );
    }

    public List<Acceso> obtenerAccesosRecientes(Pageable pageable) {
        List<Acceso> lista =  accesoRepository.findByOrderByFechaHoraEntradaDesc(pageable);
        if (lista.isEmpty()){
            throw new EntityNotFoundException("No hay accesos recientes");
        }
        return lista;
    }
}
