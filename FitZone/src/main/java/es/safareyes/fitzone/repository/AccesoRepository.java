package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Acceso;
import es.safareyes.fitzone.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, UUID> {

    long countByResultadoTrueAndFechaHoraSalidaIsNull();

    @Query("SELECT a FROM Acceso a WHERE a.socio = :socio AND a.fechaHoraSalida IS NULL")
    Optional<Acceso> findBySocioAndFechaHoraSalidaIsNull(@Param("socio") Socio socio);

    Optional<Acceso> findFirstBySocioAndResultadoTrueAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(
            Socio socio
    );

    boolean existsBySocioAndResultadoTrueAndFechaHoraSalidaIsNull(Socio socio);


}
