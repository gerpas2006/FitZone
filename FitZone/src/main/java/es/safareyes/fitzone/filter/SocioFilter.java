package es.safareyes.fitzone.filter;

import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.Plan;
import es.safareyes.fitzone.model.Socio;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDate;
import java.util.UUID;

public interface SocioFilter {

    static PredicateSpecification<Socio> filtrarPorEstado(Estado estado){
        return (from, criteriaBuilder) ->
                estado == null ? criteriaBuilder.and() : criteriaBuilder.equal(from.get("estado"),estado);
    }


    static PredicateSpecification<Socio> filtrarPorNombre(
            String nombre
    ) {
        return (from, criteriaBuilder) -> {
            if (nombre == null || nombre.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            from.join("usuario").get("nombre")
                    ),
                    "%" + nombre.trim().toLowerCase() + "%"
            );
        };
    }


    static PredicateSpecification<Socio> filtrarPorDni(
            String dni
    ) {
        return (from, criteriaBuilder) ->
                dni == null || dni.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(
                        from.get("dni"),
                        dni.trim().toUpperCase()
                );
    }


    static PredicateSpecification<Socio> filtarPorPlan(UUID idPlan){
        return (from, criteriaBuilder) ->
              idPlan == null ? criteriaBuilder.and() : criteriaBuilder.equal(from.join("plan").get("id"),idPlan);
    }

    static PredicateSpecification<Socio> filtrarPorFecha(
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        return (from, criteriaBuilder) -> {
            if (fechaInicio == null && fechaFin == null) {
                return criteriaBuilder.conjunction();
            }

            if (fechaInicio == null) {
                return criteriaBuilder.lessThanOrEqualTo(
                        from.get("fechaAlta"),
                        fechaFin
                );
            }

            if (fechaFin == null) {
                return criteriaBuilder.greaterThanOrEqualTo(
                        from.get("fechaAlta"),
                        fechaInicio
                );
            }

            return criteriaBuilder.between(
                    from.get("fechaAlta"),
                    fechaInicio,
                    fechaFin
            );
        };
    }

}
