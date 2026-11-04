package es.safareyes.fitzone.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Table(
        name = "reserva",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reserva_socio_actividad",
                columnNames = {"socio_id", "actividad_id"}
        )
)
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id", nullable = false)
    private Socio socio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;

}
