package com.mx.baz.incidencias.listener;

import com.mx.baz.incidencias.events.IncidenciaCreadaEvent;
import com.mx.baz.incidencias.events.IncidenciaEnProcesoEvent;
import com.mx.baz.incidencias.events.IncidenciaResueltaEvent;
import com.mx.baz.incidencias.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.mx.baz.incidencias.events.IncidenciaReabiertaEvent;
import com.mx.baz.incidencias.events.IncidenciaCanceladaEvent;
import com.mx.baz.incidencias.events.IncidenciaReasignadaEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationListener {

    private final NotificationService notificationService;
    

    @Async
    @EventListener
    public void onIncidenciaCreada(IncidenciaCreadaEvent event) {

        log.info("Evento recibido: IncidenciaCreadaEvent");

        notificationService.notificarNuevaIncidencia(
                event.getIncidencia()
        );

    }
    
    @Async
    @EventListener
    public void onIncidenciaEnProceso(IncidenciaEnProcesoEvent event) {
    	
    	log.info("Evento recibido: IncidenciaEnProcesoEvent");
    	
        notificationService.notificarIncidenciaEnProceso(
                event.getIncidencia(),
                event.getUsuario(),
                event.getComentario()
        );

    }
    
    @Async
    @EventListener
    public void onIncidenciaResuelta(IncidenciaResueltaEvent event) {
    	
    	 log.info("Evento recibido: IncidenciaResueltaEvent");
    	
        notificationService.notificarIncidenciaResuelta(
                event.getIncidencia(),
                event.getUsuario(),
                event.getComentario()
        );

    }
    
    @EventListener
    public void manejarIncidenciaReabierta(
            IncidenciaReabiertaEvent event) {

        log.info(
                "Evento recibido: IncidenciaReabiertaEvent. Folio: {}",
                event.getIncidencia().getFolio()
        );

        notificationService.notificarIncidenciaReabierta(
                event.getIncidencia(),
                event.getMotivo()
        );
    }
    
    @Async
    @EventListener
    public void onIncidenciaCancelada(
            IncidenciaCanceladaEvent event
    ) {

        log.info(
                "Evento recibido: IncidenciaCanceladaEvent. Folio: {}",
                event.getIncidencia().getFolio()
        );

        notificationService.notificarIncidenciaCancelada(
                event.getIncidencia(),
                event.getUsuario(),
                event.getComentario()
        );
    }
    
    @Async
    @EventListener
    public void onIncidenciaReasignada(
            IncidenciaReasignadaEvent event
    ) {

        log.info(
                "Evento recibido: IncidenciaReasignadaEvent. Folio: {}",
                event.getIncidencia().getFolio()
        );

        notificationService.notificarIncidenciaReasignada(
                event.getIncidencia(),
                event.getEmpleadoAnterior(),
                event.getEmpleadoNuevo(),
                event.getUsuario(),
                event.getComentario()
        );
    }

}