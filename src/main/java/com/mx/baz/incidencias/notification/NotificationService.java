package com.mx.baz.incidencias.notification;

import com.mx.baz.incidencias.entity.Incidencia;

public interface NotificationService {

    void notificarNuevaIncidencia(Incidencia incidencia);

    void notificarIncidenciaResuelta(Incidencia incidencia, String usuario, String comentario);
    
    void notificarIncidenciaEnProceso(Incidencia incidencia, String usuario, String comentario);
}