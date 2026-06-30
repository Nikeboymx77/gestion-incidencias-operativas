package com.mx.baz.incidencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_incidencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialIncidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incidencia_id")
    private Incidencia incidencia;

    private String accion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    private String usuario;

    private LocalDateTime fechaEvento;

    @PrePersist
    public void prePersist() {
        this.fechaEvento = LocalDateTime.now();
    }
}
