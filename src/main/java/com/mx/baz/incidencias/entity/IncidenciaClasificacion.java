package com.mx.baz.incidencias.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import com.mx.baz.incidencias.enums.EstadoClasificacion;

@Entity
@Table(
    name = "incidencia_clasificacion",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_clasificacion_incidencia",
            columnNames = "incidencia_id"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidenciaClasificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incidencia_id", nullable = false)
    private Incidencia incidencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaProblema categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regla_id")
    private ReglaClasificacion regla;

    @Column(name = "version_regla")
    private Integer versionRegla;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoClasificacion estado = EstadoClasificacion.SIN_CLASIFICAR;

    @Column(name = "fecha_clasificacion")
    private LocalDateTime fechaClasificacion;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "revisado_por", length = 100)
    private String revisadoPor;
}
