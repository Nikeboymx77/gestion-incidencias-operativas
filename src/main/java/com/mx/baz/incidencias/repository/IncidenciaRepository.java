package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.Incidencia;
import com.mx.baz.incidencias.enums.EstadoIncidencia;
import com.mx.baz.incidencias.repository.projection.RankingEmpleadoProjection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.mx.baz.incidencias.enums.EstadoIncidencia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    
    List<Incidencia> findByEmpleadoAsignadoUsernameTelegramAndEstadoInOrderByFechaCorreoAsc(
            String usernameTelegram,
            Collection<EstadoIncidencia> estados
    );
    
    List<Incidencia>
    findByEmpleadoAsignadoNombreContainingIgnoreCaseAndEstadoOrderByFechaCorreoAsc(
            String nombreEmpleado,
            EstadoIncidencia estado
    );
    
    long countByEmpleadoAsignadoIdAndEstado(
            Long empleadoId,
            EstadoIncidencia estado
    );
    
    long countByEstadoAndFechaResolucionBetween(
            EstadoIncidencia estado,
            LocalDateTime inicio,
            LocalDateTime fin
    );
    
    @Query("""
    	    SELECT
    	        e.id AS empleadoId,
    	        e.nombre AS nombre,

    	        SUM(CASE
    	            WHEN i.estado = com.mx.baz.incidencias.enums.EstadoIncidencia.PENDIENTE
    	            THEN 1 ELSE 0
    	        END) AS pendientes,

    	        SUM(CASE
    	            WHEN i.estado = com.mx.baz.incidencias.enums.EstadoIncidencia.EN_PROCESO
    	            THEN 1 ELSE 0
    	        END) AS enProceso,

    	        SUM(CASE
    	            WHEN i.estado = com.mx.baz.incidencias.enums.EstadoIncidencia.REABIERTA
    	            THEN 1 ELSE 0
    	        END) AS reabiertas,

    	        SUM(CASE
    	            WHEN i.estado = com.mx.baz.incidencias.enums.EstadoIncidencia.RESUELTA
    	            THEN 1 ELSE 0
    	        END) AS resueltas,

    	        SUM(CASE
    	            WHEN i.estado IN (
    	                com.mx.baz.incidencias.enums.EstadoIncidencia.PENDIENTE,
    	                com.mx.baz.incidencias.enums.EstadoIncidencia.EN_PROCESO,
    	                com.mx.baz.incidencias.enums.EstadoIncidencia.REABIERTA
    	            )
    	            THEN 1 ELSE 0
    	        END) AS totalActivas

    	    FROM Incidencia i
    	    JOIN i.empleadoAsignado e

    	    WHERE e.activo = true

    	    GROUP BY e.id, e.nombre

    	    ORDER BY
    	        SUM(CASE
    	            WHEN i.estado = com.mx.baz.incidencias.enums.EstadoIncidencia.RESUELTA
    	            THEN 1 ELSE 0
    	        END) DESC,
    	        e.nombre ASC
    	    """)
    	List<RankingEmpleadoProjection> obtenerRankingEmpleados();
}
