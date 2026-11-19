package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.AccesoResponseDto;
import es.safareyes.fitzone.dto.AforoResponseDto;
import es.safareyes.fitzone.exception.ElementoNoEncontrado;
import es.safareyes.fitzone.model.Acceso;
import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.EstadoCuota;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.repository.AccesoRepository;
import es.safareyes.fitzone.repository.CuotaRepository;
import es.safareyes.fitzone.repository.SocioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccesoService {

    public static final long AFORO_MAXIMO = 150;

    public static final String INACTIVO = "INACTIVO";
    public static final String CUOTA_PENDIENTE = "CUOTA_PENDIENTE";
    public static final String YA_DENTRO = "YA_DENTRO";
    public static final String AFORO_COMPLETO = "AFORO_COMPLETO";

    private final AccesoRepository accesoRepository;
    private final SocioRepository socioRepository;
    private final CuotaRepository cuotaRepository;

    /**
     * RN-05 / RN-06: valida la entrada y registra SIEMPRE el intento
     * (si el DNI existe). Un DNI inexistente devuelve 404 y no se registra.
     */
    @Transactional
    public Acceso tornoEntrada(String dni) {
        Socio socio = socioRepository.findByDni(normalizarDni(dni))
                .orElseThrow(() -> new ElementoNoEncontrado(
                        "No se ha encontrado el socio con ese DNI"
                ));

        Acceso acceso = new Acceso();
        acceso.setSocio(socio);
        acceso.setFechaHoraEntrada(LocalDateTime.now());
        acceso.setFechaHoraSalida(null);
        acceso.setResultado(false);

        String motivo = motivoRechazo(socio);

        if (motivo != null) {
            acceso.setMotivoRechazo(motivo);
            return accesoRepository.save(acceso);
        }

        acceso.setResultado(true);
        acceso.setMotivoRechazo(null);

        return accesoRepository.save(acceso);
    }

    private String motivoRechazo(Socio socio) {
        if (socio.getEstado() != Estado.ACTIVO) {
            return INACTIVO;
        }

        LocalDate hoy = LocalDate.now();
        boolean cuotaPagada = cuotaRepository.existsBySocioIdAndMesAndAnioAndEstadoCuota(
                socio.getId(),
                hoy.getMonthValue(),
                hoy.getYear(),
                EstadoCuota.PAGADA
        );
        if (!cuotaPagada) {
            return CUOTA_PENDIENTE;
        }

        if (accesoRepository.existsBySocioAndResultadoTrueAndFechaHoraSalidaIsNull(socio)) {
            return YA_DENTRO;
        }

        if (aforoActual() >= AFORO_MAXIMO) {
            return AFORO_COMPLETO;
        }

        return null;
    }

    /**
     * RN-07: una salida sin entrada abierta devuelve 422.
     */
    @Transactional
    public AccesoResponseDto tornoSalida(String dni) {
        Socio socio = socioRepository.findByDni(normalizarDni(dni))
                .orElseThrow(() -> new ElementoNoEncontrado(
                        "No se ha encontrado el socio con ese DNI"
                ));

        Acceso accesoAbierto = accesoRepository
                .findAccesosAbiertosBySocio(socio)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "El socio no tiene una entrada abierta"
                ));

        accesoAbierto.setFechaHoraSalida(LocalDateTime.now());

        return AccesoResponseDto.of(accesoRepository.save(accesoAbierto));
    }

    /**
     * RN-07: aforo = entradas permitidas del día sin salida registrada.
     */
    public long aforoActual() {
        LocalDate hoy = LocalDate.now();
        return accesoRepository.countEntradasAbiertasDelDia(
                hoy.atStartOfDay(),
                hoy.plusDays(1).atStartOfDay()
        );
    }

    public AforoResponseDto obtenerAforo() {
        return new AforoResponseDto(aforoActual(), AFORO_MAXIMO);
    }

    public List<Acceso> obtenerSociosDentro() {
        LocalDate hoy = LocalDate.now();

        return accesoRepository.findSociosDentro(
                hoy.atStartOfDay(),
                hoy.plusDays(1).atStartOfDay()
        );
    }

    public List<Acceso> obtenerAccesosRecientes(Pageable pageable) {
        List<Acceso> lista = accesoRepository.findByOrderByFechaHoraEntradaDesc(pageable);
        if (lista.isEmpty()) {
            throw new ElementoNoEncontrado("No hay accesos recientes");
        }
        return lista;
    }

    private String normalizarDni(String dni) {
        return dni == null ? null : dni.trim().toUpperCase();
    }
}
