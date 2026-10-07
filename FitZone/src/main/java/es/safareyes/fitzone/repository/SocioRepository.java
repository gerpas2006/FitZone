package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.Plan;
import es.safareyes.fitzone.model.Socio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

@Repository
public interface SocioRepository extends JpaRepository<Socio, UUID>, JpaSpecificationExecutor<Socio> {

    Optional<Socio> findByDni(String dni);
    List<Socio> findByNombre(String nombre);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"cuotas", "plan"})
    @Query("""
        SELECT DISTINCT c.socio
        FROM Cuota c
        JOIN c.socio s
        WHERE c.mes = :mes
          AND c.anio = :anio
          AND c.estadoCuota = EstadoCuota.PENDIENTE
        """)
    List<Socio> findSociosConCuotaPendiente(
            @Param("mes") Integer mes,
            @Param("anio") Integer anio
    );



}


