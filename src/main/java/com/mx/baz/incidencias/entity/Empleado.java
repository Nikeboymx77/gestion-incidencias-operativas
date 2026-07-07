package com.mx.baz.incidencias.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "empleados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "username_telegram")
    private String usernameTelegram;

    private String email;

    private Boolean activo;

    @Column(name = "ultima_asignacion")
    private LocalDateTime ultimaAsignacion;
    
    @OneToMany(
            mappedBy = "empleado",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<EmpleadoDiaLaboral> diasLaborales = new ArrayList<>();

    @OneToMany(
            mappedBy = "empleado",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<EmpleadoAusencia> ausencias = new ArrayList<>();
}
