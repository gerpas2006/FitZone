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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "dni", unique = true)
    private String dni;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    @Column(name = "apellidos", nullable = false)
    private  String apellidos;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "telefono", unique = true)
    private String telefono;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;
    @Column(name = "matricula", unique = true)
    private String matricula;
    @Column(name = "estado")
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

    @OneToOne(mappedBy = "socio")
    private Usuario usuario;

}
