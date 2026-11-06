package es.safareyes.fitzone.repository;

import es.safareyes.fitzone.model.Acceso;
import es.safareyes.fitzone.model.Socio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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


    @Query("SELECT a FROM Acceso a WHERE a.socio = :socio AND a.fechaHoraSalida IS NULL")
    Optional<Acceso> findBySocioAndFechaHoraSalidaIsNull(@Param("socio") Socio socio);

    @Query("""
        SELECT a
        FROM Acceso a
        JOIN FETCH a.socio s
        JOIN FETCH s.usuario
        WHERE s = :socio
          AND a.resultado = true
          AND a.fechaHoraSalida IS NULL
        ORDER BY a.fechaHoraEntrada DESC
        """)
    Optional<Acceso> findAccesoAbiertoBySocio(
            @Param("socio") Socio socio
    );

    @Query("""
            SELECT COUNT(a)
            FROM Acceso a
            WHERE a.resultado = true
              AND a.fechaHoraSalida IS NULL
              AND a.fechaHoraEntrada >= :inicio
              AND a.fechaHoraEntrada < :fin
            """)
    long countEntradasAbiertasDelDia(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );


    boolean existsBySocioAndResultadoTrueAndFechaHoraSalidaIsNull(Socio socio);

    @Query("""
        SELECT a
        FROM Acceso a
        JOIN FETCH a.socio s
        JOIN FETCH s.usuario
        WHERE a.resultado = true
          AND a.fechaHoraSalida IS NULL
          AND a.fechaHoraEntrada >= :inicio
          AND a.fechaHoraEntrada < :fin
        ORDER BY a.fechaHoraEntrada ASC
        """)
    List<Acceso> findSociosDentro(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @EntityGraph(attributePaths = {"socio", "socio.usuario"})
    List<Acceso> findByOrderByFechaHoraEntradaDesc(Pageable pageable);


}
