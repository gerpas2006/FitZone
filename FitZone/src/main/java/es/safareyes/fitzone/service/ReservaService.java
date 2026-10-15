package es.safareyes.fitzone.service;

import es.safareyes.fitzone.model.Actividad;
import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.EstadoCuota;
import es.safareyes.fitzone.model.Reserva;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.model.Usuario;
import es.safareyes.fitzone.repository.ActividadRepository;
import es.safareyes.fitzone.repository.CuotaRepository;
import es.safareyes.fitzone.repository.ReservaRepository;
import es.safareyes.fitzone.repository.SocioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ActividadRepository actividadRepository;
    private final SocioRepository socioRepository;
    private final CuotaRepository cuotaRepository;

    @Transactional
    public Reserva reservarActividad(
            UUID actividadId,
            Usuario usuario
    ) {
        LocalDateTime ahora = LocalDateTime.now();

        Socio socio = socioRepository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_CONTENT,
                        "El usuario no tiene un socio asociado"
                ));

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado la actividad indicada"
                ));

        if (actividad.getFechaHora() == null
                || !actividad.getFechaHora().isAfter(ahora)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "No puedes reservar una actividad que ya ha comenzado"
            );
        }

        if (socio.getEstado() != Estado.ACTIVO) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "Solo los socios activos pueden reservar actividades"
            );
        }

        if (socio.getPlan() == null
                || !socio.getPlan().isInluyeClases()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "El plan del socio no incluye actividades"
            );
        }

        boolean tieneCuotaPendiente =
                cuotaRepository.existsBySocioIdAndEstadoCuota(
                        socio.getId(),
                        EstadoCuota.PENDIENTE
                );

        if (tieneCuotaPendiente) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "El socio tiene cuotas pendientes de pago"
            );
        }

        boolean yaEstaReservado =
                reservaRepository.existsBySocioIdAndActividadId(
                        socio.getId(),
                        actividad.getId()
                );

        if (yaEstaReservado) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "El socio ya está apuntado a esta actividad"
            );
        }

        long reservasFuturas =
                reservaRepository.countBySocioIdAndActividadFechaHoraAfter(
                        socio.getId(),
                        ahora
                );

        if (reservasFuturas >= 3) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "No puedes tener más de 3 reservas futuras"
            );
        }

        long reservasActuales =
                reservaRepository.contarAforoActual(actividad.getId());

        if (reservasActuales >= actividad.getAforoMaximo()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "No quedan plazas libres para esta actividad"
            );
        }

        Reserva reserva = Reserva.builder()
                .fechaReserva(ahora)
                .socio(socio)
                .actividad(actividad)
                .build();

        return reservaRepository.save(reserva);
    }
}