package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, UUID> {
}
