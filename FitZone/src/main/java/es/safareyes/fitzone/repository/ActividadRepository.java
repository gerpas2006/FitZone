package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.dto.ActividadResponseDto;
import es.safareyes.fitzone.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, UUID> {

    @Query("""
        SELECT new es.safareyes.fitzone.dto.ActividadResponseDto(
            a.id,
            a.nombre,
            a.fechaHora,
            a.duracionMinutos,
            a.sala,
            a.aforoMaximo,
            COUNT(r),
            a.aforoMaximo - COUNT(r),
            m.id
        )
        FROM Actividad a
        LEFT JOIN a.reservas r
        LEFT JOIN a.monitor m
        WHERE a.fechaHora >= :inicio
          AND a.fechaHora < :fin
        GROUP BY
            a.id,
            a.nombre,
            a.fechaHora,
            a.duracionMinutos,
            a.sala,
            a.aforoMaximo,
            m.id
        HAVING COUNT(r) < a.aforoMaximo
        ORDER BY a.fechaHora
        """)
    List<ActividadResponseDto> findActividadesDelDia(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}
