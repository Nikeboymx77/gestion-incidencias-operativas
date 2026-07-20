package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.mx.baz.incidencias.enums.EstadoIncidencia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    Optional<Incidencia> findByFolio(String folio);

    boolean existsByFolio(String folio);

    long countByEstado(EstadoIncidencia estado);

    List<Incidencia> findTop20ByOrderByCreatedAtDesc();
    
    List<Incidencia> findByEstado(EstadoIncidencia estado);
    
    @Query("""
            SELECT i
            FROM Incidencia i
            WHERE (:texto IS NULL
                   OR LOWER(i.folio) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(i.nombreCliente) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(i.clienteUnico) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(i.sucursal) LIKE LOWER(CONCAT('%', :texto, '%')))
              AND (:estado IS NULL OR i.estado = :estado)
            ORDER BY i.createdAt DESC
            """)
    List<Incidencia> buscarParaDashboard(
            @Param("texto") String texto,
            @Param("estado") EstadoIncidencia estado
    );
}