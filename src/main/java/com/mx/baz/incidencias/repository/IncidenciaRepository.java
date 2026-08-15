package com.mx.baz.incidencias.repository;

import com.mx.baz.incidencias.entity.Empleado;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mx.baz.incidencias.enums.PrioridadIncidencia;

import com.mx.baz.incidencias.repository.projection.DashboardMetricasProjection;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    Optional<Incidencia> findByFolio(String folio);

    boolean existsByFolio(String folio);

    long countByEstado(EstadoIncidencia estado);

    List<Incidencia> findTop20ByOrderByCreatedAtDesc();
    
    List<Incidencia> findByEstado(EstadoIncidencia estado);
    
       
    
    @Query("""
            SELECT i
            FROM Incidencia i
            LEFT JOIN i.empleadoAsignado e
            WHERE (
                :texto IS NULL
                OR LOWER(i.folio) LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(i.nombreCliente) LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(i.clienteUnico) LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(i.sucursal) LIKE LOWER(CONCAT('%', :texto, '%'))
            )
            AND (
                :estado IS NULL
                OR i.estado = :estado
            )
            AND (
			    :prioridad IS NULL
			    OR i.prioridad = :prioridad
			)
			AND (
			    :carpetaOrigen IS NULL
			    OR i.carpetaOrigen = :carpetaOrigen
			)
            AND (
                :empleadoId IS NULL
                OR e.id = :empleadoId
            )
            AND (
                :fechaDesde IS NULL
                OR i.createdAt >= :fechaDesde
            )
            AND (
                :fechaHasta IS NULL
                OR i.createdAt < :fechaHasta
            )
            
            """)
    Page<Incidencia> buscarParaDashboard(
            @Param("texto") String texto,
            @Param("estado") EstadoIncidencia estado,
            @Param("empleadoId") Long empleadoId,
            @Param("prioridad") PrioridadIncidencia prioridad,
            @Param("carpetaOrigen") String carpetaOrigen,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta,
            Pageable pageable
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
    
    @Query("""
    	    SELECT i
    	    FROM Incidencia i
    	    LEFT JOIN FETCH i.empleadoAsignado e
    	    WHERE i.estado IN (
    	        com.mx.baz.incidencias.enums.EstadoIncidencia.PENDIENTE,
    	        com.mx.baz.incidencias.enums.EstadoIncidencia.EN_PROCESO,
    	        com.mx.baz.incidencias.enums.EstadoIncidencia.REABIERTA
    	    )
    	    ORDER BY i.fechaInicio ASC
    	    """)
    	List<Incidencia> obtenerIncidenciasAbiertas();
    
    @Query("""
            SELECT
                COUNT(i.id) AS total,

                COALESCE(
                    SUM(
                        CASE
                            WHEN i.estado =
                                com.mx.baz.incidencias.enums.EstadoIncidencia.PENDIENTE
                            THEN 1
                            ELSE 0
                        END
                    ),
                    0
                ) AS pendientes,

                COALESCE(
                    SUM(
                        CASE
                            WHEN i.estado =
                                com.mx.baz.incidencias.enums.EstadoIncidencia.EN_PROCESO
                            THEN 1
                            ELSE 0
                        END
                    ),
                    0
                ) AS enProceso,

                COALESCE(
                    SUM(
                        CASE
                            WHEN i.estado =
                                com.mx.baz.incidencias.enums.EstadoIncidencia.RESUELTA
                            THEN 1
                            ELSE 0
                        END
                    ),
                    0
                ) AS resueltas,

                COALESCE(
                    SUM(
                        CASE
                            WHEN i.estado =
                                com.mx.baz.incidencias.enums.EstadoIncidencia.REABIERTA
                            THEN 1
                            ELSE 0
                        END
                    ),
                    0
                ) AS reabiertas,

                COALESCE(
                    SUM(
                        CASE
                            WHEN i.estado =
                                com.mx.baz.incidencias.enums.EstadoIncidencia.CANCELADA
                            THEN 1
                            ELSE 0
                        END
                    ),
                    0
                ) AS canceladas

            FROM Incidencia i

            LEFT JOIN i.empleadoAsignado e

            WHERE (
                :texto IS NULL
                OR LOWER(i.folio)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(i.nombreCliente)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(i.clienteUnico)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(i.sucursal)
                    LIKE LOWER(CONCAT('%', :texto, '%'))
            )

            AND (
                :estado IS NULL
                OR i.estado = :estado
            )

            AND (
                :empleadoId IS NULL
                OR e.id = :empleadoId
            )

            AND (
                :fechaDesde IS NULL
                OR i.createdAt >= :fechaDesde
            )

            AND (
                :fechaHasta IS NULL
                OR i.createdAt < :fechaHasta
            )
            AND (
			    :prioridad IS NULL
			    OR i.prioridad = :prioridad
			)
			AND (
			    :carpetaOrigen IS NULL
			    OR i.carpetaOrigen = :carpetaOrigen
			)
            """)
    DashboardMetricasProjection obtenerMetricasDashboard(
            @Param("texto") String texto,
            @Param("estado") EstadoIncidencia estado,
            @Param("empleadoId") Long empleadoId,
            @Param("prioridad") PrioridadIncidencia prioridad,
            @Param("carpetaOrigen") String carpetaOrigen,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta
    );
    
    @Query("""
            SELECT DISTINCT i.carpetaOrigen
            FROM Incidencia i
            WHERE i.carpetaOrigen IS NOT NULL
              AND TRIM(i.carpetaOrigen) <> ''
            ORDER BY i.carpetaOrigen
            """)
    List<String> obtenerOrigenesDisponibles();
}
