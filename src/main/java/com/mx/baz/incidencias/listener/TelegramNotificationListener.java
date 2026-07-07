package com.mx.baz.incidencias.listener;

import com.mx.baz.incidencias.events.IncidenciaCreadaEvent;
import com.mx.baz.incidencias.events.IncidenciaEnProcesoEvent;
import com.mx.baz.incidencias.events.IncidenciaResueltaEvent;
import com.mx.baz.incidencias.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationListener {

    private final NotificationService notificationService;

    @EventListener
    public void onIncidenciaCreada(IncidenciaCreadaEvent event) {

        log.info("Evento recibido: IncidenciaCreadaEvent");

        notificationService.notificarNuevaIncidencia(
                event.getIncidencia()
        );

    }
    
    @EventListener
    public void onIncidenciaEnProceso(IncidenciaEnProcesoEvent event) {
    	
    	log.info("Evento recibido: IncidenciaEnProcesoEvent");
    	
        notificationService.notificarIncidenciaEnProceso(
                event.getIncidencia(),
                event.getUsuario(),
                event.getComentario()
        );

    }
    
    @EventListener
    public void onIncidenciaResuelta(IncidenciaResueltaEvent event) {
    	
    	 log.info("Evento recibido: IncidenciaResueltaEvent");
    	
        notificationService.notificarIncidenciaResuelta(
                event.getIncidencia(),
                event.getUsuario(),
                event.getComentario()
        );

    }

}