package com.mx.baz.incidencias.integration.mail.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "correos_procesados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorreoProcesado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_correo", nullable = false, unique = true)
    private String idCorreo;

    @Column(name = "folio_incidencia")
    private String folioIncidencia;

    @Column(name = "fecha_procesado")
    private LocalDateTime fechaProcesado;

    @PrePersist
    public void prePersist() {
        this.fechaProcesado = LocalDateTime.now();
    }
}
