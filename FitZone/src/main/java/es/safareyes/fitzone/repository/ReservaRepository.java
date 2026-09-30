package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    long countBySocioIdAndActividadFechaHoraAfter(
            UUID socioId,
            LocalDateTime fecha
    );

    @Query("""
            SELECT COUNT(r)
            FROM Reserva r
            WHERE r.actividad.id = :actividadId
            """)
    long contarAforoActual(
            @Param("actividadId") UUID actividadId
    );

    
}
