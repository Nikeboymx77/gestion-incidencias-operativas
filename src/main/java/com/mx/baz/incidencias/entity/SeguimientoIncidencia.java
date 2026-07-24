package com.mx.baz.incidencias.entity;

import com.mx.baz.incidencias.enums.TipoSeguimiento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "seguimiento_incidencia",
        indexes = {
                @Index(
                        name = "idx_seguimiento_incidencia_id",
                        columnList = "incidencia_id"
                ),
                @Index(
                        name = "idx_seguimiento_fecha_correo",
                        columnList = "fecha_correo"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoIncidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incidencia_id", nullable = false)
    private Incidencia incidencia;

    @Column(length = 500)
    private String asunto;

    @Column(length = 300)
    private String remitente;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_correo")
    private LocalDateTime fechaCorreo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoSeguimiento tipo;

    @Column(name = "requiere_atencion", nullable = false)
    private boolean requiereAtencion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}
