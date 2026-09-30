package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Cuota;
import es.safareyes.fitzone.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, UUID> {

    boolean existsBySocioIdAndMesAndAnio(
            UUID socioId,
            Integer mes,
            Integer anio
    );
}
