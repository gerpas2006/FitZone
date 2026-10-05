package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.dto.IngresosPorMesPlan;
import es.safareyes.fitzone.model.Cuota;
import es.safareyes.fitzone.model.EstadoCuota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, UUID> {

    boolean existsBySocioIdAndMesAndAnioAndEstadoCuota(
            UUID socioId,
            Integer mes,
            Integer anio,
            EstadoCuota estadoCuota
    );

    boolean existsBySocioIdAndMesAndAnio(
            UUID socioId,
            Integer mes,
            Integer anio
    );

    @EntityGraph(attributePaths = {"socio", "plan"})
    Page<Cuota> findBySocio_Id(UUID id, Pageable pageable);


    @Query("""
    SELECT new es.safareyes.fitzone.dto.IngresosPorMesPlan(
        c.mes,
        c.anio,
        c.plan.nombre,
        SUM(c.importe)
    )
    FROM Cuota c
    WHERE c.anio = :anio
      AND c.estadoCuota = es.safareyes.fitzone.model.EstadoCuota.PAGADA
    GROUP BY c.mes, c.anio, c.plan.nombre
    ORDER BY c.mes, c.plan.nombre
    """)
    List<IngresosPorMesPlan> obtenerIngresosPorMesYPlan(
            @Param("anio") Integer anio
    );

}
