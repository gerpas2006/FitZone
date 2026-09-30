package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, UUID> {

    Optional<Acceso> findFirstBySocioIdOrderByFechaHoraEntradaDesc(
            UUID socioId
    );
}
