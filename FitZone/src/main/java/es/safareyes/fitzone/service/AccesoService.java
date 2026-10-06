package es.safareyes.fitzone.service;

import es.safareyes.fitzone.model.Acceso;
import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.EstadoCuota;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.repository.AccesoRepository;
import es.safareyes.fitzone.repository.SocioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        long aforoActual = accesoRepository.countByResultadoTrueAndFechaHoraSalidaIsNull();

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

    public Acceso tornoSalida(String dni) {
        Socio socioBuscado = socioRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado el socio con ese DNI"
                ));

        Acceso accesoExistente = accesoRepository
                .findFirstBySocioAndResultadoTrueAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(
                        socioBuscado
                )
                .orElseThrow(() -> new EntityNotFoundException(
                        "El socio no se encuentra dentro del gimnasio"
                ));

        accesoExistente.setFechaHoraSalida(LocalDateTime.now());

        return accesoRepository.save(accesoExistente);
    }

    public long personaDentroGym(){
        return accesoRepository.countByResultadoTrueAndFechaHoraSalidaIsNull();
    }
}
