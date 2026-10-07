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
@Table(name = "acceso")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "fecha_hora_entrada")
    private LocalDateTime fechaHoraEntrada;
    @Column(name = "fecha_hora_salida")
    private LocalDateTime fechaHoraSalida;

    private boolean resultado;

    private String motivoRechazo;
    private boolean bonificacionParkingAplicada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id", nullable = false)
    private Socio socio;

}
