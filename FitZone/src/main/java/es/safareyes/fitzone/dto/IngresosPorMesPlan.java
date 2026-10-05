package es.safareyes.fitzone.dto;

public record IngresosPorMesPlan(
        Integer mes,
        Integer anio,
        String plan,
        Double ingresos
) {
}
