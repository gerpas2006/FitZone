package es.safareyes.fitzone.service;

import es.safareyes.fitzone.dto.GenerarCuotasResponse;
import es.safareyes.fitzone.dto.IngresosPorMesPlan;
import es.safareyes.fitzone.model.Cuota;
import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.EstadoCuota;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.repository.CuotaRepository;
import es.safareyes.fitzone.repository.SocioRepository;
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
    private final SocioRepository socioRepository;

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

    private void validarPeriodo(Integer mes, Integer anio) {
        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException(
                    "El mes debe estar comprendido entre 1 y 12"
            );
        }

        if (anio == null || anio < 2000) {
            throw new IllegalArgumentException(
                    "El año no es válido"
            );
        }
    }

    @Transactional
    public GenerarCuotasResponse generarCuotas(Integer mes, Integer anio) {
        validarPeriodo(mes, anio);

        int cuotasCreadas = 0;
        int sociosInactivos = 0;
        long cuotasPendientes;

        List<Socio> socios = socioRepository.findAll();

        for (Socio socio : socios) {
            cuotasPendientes = cuotaRepository
                    .countBySocioIdAndEstadoCuota(
                            socio.getId(),
                            EstadoCuota.PENDIENTE
                    );

            if (cuotasPendientes >= 2 && socio.getEstado() == Estado.ACTIVO) {
                socio.setEstado(Estado.INACTIVO);
                socioRepository.save(socio);
                sociosInactivos++;
            }
        }

        for (Socio socio : socios) {
            if (socio.getEstado() != Estado.ACTIVO) {
                continue;
            }

            boolean yaTieneCuota = cuotaRepository
                    .existsBySocioIdAndMesAndAnio(
                            socio.getId(),
                            mes,
                            anio
                    );

            if (yaTieneCuota) {
                continue;
            }

            Cuota cuota = new Cuota();
            cuota.setMes(mes);
            cuota.setAnio(anio);
            cuota.setImporte(socio.getPlan().getPrecioMensual());
            cuota.setEstadoCuota(EstadoCuota.PENDIENTE);
            cuota.setSocio(socio);
            cuota.setPlan(socio.getPlan());

            cuotaRepository.save(cuota);
            cuotasCreadas++;
        }

        return new GenerarCuotasResponse(
                mes,
                anio,
                cuotasCreadas,
                sociosInactivos
        );

    }
}

