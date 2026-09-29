package es.safareyes.fitzone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
     private String nombre;
     private double precioMensual;
     @Column(name = "incluye_clases")
     private boolean inluyeClases;
     @Enumerated(EnumType.STRING )
     private TipoPlan tipoPlan;

    @OneToMany(mappedBy = "plan")
    private List<Socio> socios;

    @OneToMany(mappedBy = "plan")
    private List<Cuota> cuotas;
}
