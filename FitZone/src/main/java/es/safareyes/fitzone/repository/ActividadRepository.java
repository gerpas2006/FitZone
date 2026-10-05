package es.safareyes.fitzone.repository;

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
        SELECT a
        FROM Actividad a
        WHERE a.fechaHora >= :inicio
        ORDER BY a.fechaHora
        """)
    List<Actividad> findActividadesDelDia(
            @Param("inicio") LocalDateTime inicio
    );
}
