package es.safareyes.fitzone.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Table(name = "socio")
public class Socio {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true)
    private String dni;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private  String apellidos;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String telefono;

    private LocalDate fechaAlta;
    @Column(unique = true)
    private String matricula;
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuota> cuotas;

    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Acceso> accesos;

    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;


}
