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

    public boolean tornoEntrada(String dni) {
        Socio socioBuscado = socioRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el socio con ese DNI"));

        if (!socioBuscado.getEstado().equals(Estado.ACTIVO)) {
            return false;
        }

        if (socioBuscado.getCuotas().equals(EstadoCuota.PENDIENTE)) {
            return false;
        }

        long aforoActual = accesoRepository.countByResultadoTrueAndFechaHoraSalidaIsNull();
        if (aforoActual >= 150) {
            return false;
        }

        Acceso nuevoAcceso = new Acceso();
        nuevoAcceso.setSocio(socioBuscado);
        nuevoAcceso.setFechaHoraEntrada(LocalDateTime.now());
        nuevoAcceso.setResultado(true);
        nuevoAcceso.setFechaHoraSalida(null);

        accesoRepository.save(nuevoAcceso);

        return true;
    }

    public boolean tornoSalida(String dni) {
        Socio socioBuscado = socioRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el socio con ese DNI"));

        Optional<Acceso> accesoOpt = accesoRepository.findBySocioAndFechaHoraSalidaIsNull(socioBuscado);

        if (accesoOpt.isEmpty()) {
            return false;
        }

        Acceso accesoExistente = accesoOpt.get();
        accesoExistente.setFechaHoraSalida(LocalDateTime.now());

        accesoRepository.save(accesoExistente);

        return true;
    }

    public long personaDentroGym(){
        return accesoRepository.countByResultadoTrueAndFechaHoraSalidaIsNull();
    }
}
