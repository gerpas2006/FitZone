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

    static PredicateSpecification<Socio> filtarPorPlan(UUID idPlan){
        return (from, criteriaBuilder) ->
              idPlan == null ? criteriaBuilder.and() : criteriaBuilder.equal(from.join("plan").get("id"),idPlan);
    }
    static PredicateSpecification<Socio> filtrarPorFecha(LocalDate fechaInicio, LocalDate fechaFin){
       LocalDate fechaInicioReal = fechaInicio == null ? LocalDate.now() : fechaInicio;
       LocalDate fechaFinReal = fechaFin == null ? LocalDate.now() : fechaFin;

       return (from, criteriaBuilder) ->
               criteriaBuilder.between(from.get("fechaAlta"),fechaInicioReal,fechaFinReal);
}

}
