package es.safareyes.fitzone.dto;

import es.safareyes.fitzone.model.Plan;
import es.safareyes.fitzone.model.Socio;
import es.safareyes.fitzone.model.TipoPlan;

import java.util.UUID;

public record PlanResponseDto(
        UUID id,
        String nombre,
        double precioMensual,
        boolean inluyeClases,
        TipoPlan tipoPlan
) {

    public static PlanResponseDto of(Plan plan) {
        return new PlanResponseDto(
                plan.getId(),
                plan.getNombre(),
                plan.getPrecioMensual(),
                plan.isInluyeClases(),
                plan.getTipoPlan()
        );
    }
}
