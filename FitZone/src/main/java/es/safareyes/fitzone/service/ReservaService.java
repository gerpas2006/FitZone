package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.CrearReservaRequest;
import es.safareyes.fitzone.dto.ReservaResponseDto;
import es.safareyes.fitzone.model.Actividad;
import es.safareyes.fitzone.model.Cuota;
import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.EstadoCuota;
import es.safareyes.fitzone.model.Reserva;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.repository.ActividadRepository;
import es.safareyes.fitzone.repository.CuotaRepository;
import es.safareyes.fitzone.repository.ReservaRepository;
import es.safareyes.fitzone.repository.SocioRepository;
import es.safareyes.fitzone.exception.ElementoNoEncontrado;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SocioRepository socioRepository;
    private final ActividadRepository actividadRepository;
    private final CuotaRepository cuotaRepository;

    @Transactional
    public ReservaResponseDto crearReserva(
            Usuario usuario,
            CrearReservaRequest request
    ) {
        // Las reservas se asocian al socio del usuario autenticado.
        Socio socio = socioRepository
                .findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new ElementoNoEncontrado(
                        "El usuario autenticado no tiene un socio asociado"
                ));

        Actividad actividad = actividadRepository
                .findById(request.actividadId())
                .orElseThrow(() -> new ElementoNoEncontrado(
                        "No se ha encontrado la actividad"
                ));

        // Valida todas las condiciones antes de guardar la reserva.
        validarSocio(socio);
        validarActividadFutura(actividad);
        validarCuotaPagadaEsteMes(socio);
        validarReservaDuplicada(socio, actividad);
        validarLimiteReservas(socio);
        validarPlazasDisponibles(actividad);

        Reserva reserva = CrearReservaRequest.toEntity(
                request,
                socio,
                actividad
        );

        Reserva reservaGuardada = reservaRepository.save(reserva);

        return ReservaResponseDto.of(reservaGuardada);
    }

    private void validarSocio(Socio socio) {
        if (socio.getEstado() != Estado.ACTIVO) {
            // Los socios inactivos no pueden reservar actividades.
            rechazar("El socio está inactivo");
        }

        if (socio.getPlan() == null || !socio.getPlan().isInluyeClases()) {
            rechazar("El plan del socio no incluye actividades");
        }
    }

    private void validarActividadFutura(Actividad actividad) {
        if (actividad.getFechaHora() == null
                || !actividad.getFechaHora().isAfter(LocalDateTime.now())) {
            rechazar("La actividad debe ser futura");
        }
    }

    private void validarCuotaPagadaEsteMes(Socio socio) {
        LocalDate hoy = LocalDate.now();

        Cuota cuota = cuotaRepository
                .findBySocioIdAndMesAndAnio(
                        socio.getId(),
                        hoy.getMonthValue(),
                        hoy.getYear()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "El socio no tiene cuota del mes actual"
                ));

        if (cuota.getEstadoCuota() != EstadoCuota.PAGADA) {
            // Una cuota pendiente impide crear la reserva.
            rechazar("El socio tiene la cuota del mes pendiente");
        }
    }

    private void validarReservaDuplicada(
            Socio socio,
            Actividad actividad
    ) {
        boolean yaExiste = reservaRepository.existsBySocioIdAndActividadId(
                socio.getId(),
                actividad.getId()
        );

        if (yaExiste) {
            // Evita duplicar la reserva del mismo socio y actividad.
            rechazar("El socio ya tiene una reserva para esta actividad");
        }
    }

    private void validarLimiteReservas(Socio socio) {
        long reservasFuturas = reservaRepository
                .countBySocioIdAndActividadFechaHoraAfter(
                        socio.getId(),
                        LocalDateTime.now()
                );

        if (reservasFuturas >= 3) {
            // Cada socio puede mantener como máximo tres reservas futuras.
            rechazar("El socio ya tiene el máximo de 3 reservas futuras");
        }
    }

    private void validarPlazasDisponibles(Actividad actividad) {
        long reservasActuales = reservaRepository
                .contarAforoActual(actividad.getId());

        if (reservasActuales >= actividad.getAforoMaximo()) {
            // No se aceptan más reservas cuando se alcanza el aforo.
            rechazar("No quedan plazas disponibles para la actividad");
        }
    }

    private void rechazar(String mensaje) {
        throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                mensaje
        );
    }
}