package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.model.Plan;
import es.safareyes.fitzone.model.Socio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

@Repository
public interface SocioRepository extends JpaRepository<Socio, UUID> {

    List<Socio> findByEstado(Estado estado);
    List<Socio> findByDni(String dni);
    List<List<Socio>> findByNombre(String nombre);
    List<Socio> findByPlan(Plan plan);

    Page<Socio> findByFechaAlta(LocalDate fechaAlta, Pageable pageable);


}
