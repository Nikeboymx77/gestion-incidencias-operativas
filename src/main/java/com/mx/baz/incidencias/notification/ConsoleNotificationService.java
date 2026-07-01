package com.mx.baz.incidencias.notification;

import com.mx.baz.incidencias.entity.Incidencia;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
        name = "notifications.telegram.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class ConsoleNotificationService implements NotificationService {

    @Override
    public void notificarNuevaIncidencia(Incidencia incidencia) {
        log.info("Nueva incidencia creada: {} - Asignada a: {}",
                incidencia.getFolio(),
                incidencia.getEmpleadoAsignado() != null
                        ? incidencia.getEmpleadoAsignado().getNombre()
                        : "Sin asignar");
    }

    @Override
    public void notificarIncidenciaResuelta(
            Incidencia incidencia,
            String usuario,
            String comentario) {

        log.info("Incidencia resuelta: {} - Usuario: {} - Comentario: {}",
                incidencia.getFolio(),
                usuario,
                comentario);
    }
}
