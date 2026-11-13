package com.mx.baz.incidencias.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "regla_proceso_afectado")
public class ReglaProcesoAfectado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "proceso_id",
        nullable = false
    )
    private ProcesoAfectado proceso;

    @Column(
        name = "patron",
        nullable = false,
        length = 500
    )
    private String patron;

    @Column(nullable = false)
    private Integer prioridad;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;
    
    @Column(name = "origen", nullable = false, length = 20)
    private String origen = "ASUNTO";       

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Generar getters y setters
    
    public ProcesoAfectado getProceso() {
        return proceso;
    }

    public void setProceso(ProcesoAfectado proceso) {
        this.proceso = proceso;
    }
    
    
    
    public String getPatron() {
        return patron;
    }

    public Integer getPrioridad() {
        return prioridad;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
       
    
}
