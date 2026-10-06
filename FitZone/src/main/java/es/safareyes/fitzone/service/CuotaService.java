package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.IngresosPorMesPlan;
import es.safareyes.fitzone.model.Cuota;
import es.safareyes.fitzone.model.EstadoCuota;
import es.safareyes.fitzone.repository.CuotaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CuotaService {

    private final CuotaRepository cuotaRepository;

    @Transactional
    public Cuota registrarPago(UUID cuotaId) {
        Cuota cuota = cuotaRepository.findById(cuotaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se ha encontrado ninguna cuota con ese id"
                ));

        if (cuota.getEstadoCuota() == EstadoCuota.PAGADA) {
            throw new IllegalStateException(
                    "La cuota ya está pagada"
            );
        }

        cuota.setEstadoCuota(EstadoCuota.PAGADA);
        cuota.setFechaPago(LocalDateTime.now());

        return cuotaRepository.save(cuota);
    }


    @Transactional
    public List<IngresosPorMesPlan> obtenerIngresosPorMesYPlan(
            Integer anio
    ) {
        return cuotaRepository.obtenerIngresosPorMesYPlan(anio);
    }

}
