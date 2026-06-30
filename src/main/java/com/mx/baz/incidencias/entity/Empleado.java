package com.mx.baz.incidencias.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "trabaja_semana")
    private Boolean trabajaSemana;

    @Column(name = "trabaja_fin_semana")
    private Boolean trabajaFinSemana;

    @Column(name = "orden_asignacion")
    private Integer ordenAsignacion;
}
