package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.EstadoCuota;
import es.safareyes.fitzone.model.Socio;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SocioMorosoResponseDto(
        UUID id,
        String dni,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        LocalDate fechaAlta,
        Estado estado,
        UUID planId,
        long cuotasPendientes,
        List<CuotaPendienteDto> mesesPendientes
) {
    public static SocioMorosoResponseDto of(Socio socio) {
        List<CuotaPendienteDto> cuotasPendientes = socio.getCuotas()
                .stream()
                .filter(cuota -> cuota.getEstadoCuota() == EstadoCuota.PENDIENTE)
                .map(cuota -> new CuotaPendienteDto(
                        cuota.getMes(),
                        cuota.getAnio()
                ))
                .toList();

        return new SocioMorosoResponseDto(
                socio.getId(),
                socio.getDni(),
                socio.getNombre(),
                socio.getApellidos(),
                socio.getEmail(),
                socio.getTelefono(),
                socio.getFechaAlta(),
                socio.getEstado(),
                socio.getPlan().getId(),
                cuotasPendientes.size(),
                cuotasPendientes
        );
    }
}