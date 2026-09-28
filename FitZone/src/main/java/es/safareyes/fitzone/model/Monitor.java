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
@Table(name = "monitor")
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID Id;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "monitor")
    private List<Actividad> actividades;
}
