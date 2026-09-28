package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SocioRepository extends JpaRepository<Socio, UUID> {
}
