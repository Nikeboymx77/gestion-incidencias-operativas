package com.mx.baz.incidencias.notification;

import com.mx.baz.incidencias.entity.Incidencia;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "notifications.telegram.enabled",
        havingValue = "true"
)
public class TelegramNotificationService implements NotificationService {

    private final TelegramClient telegramClient;

    @Override
    public void notificarNuevaIncidencia(Incidencia incidencia) {

        String empleado = incidencia.getEmpleadoAsignado() != null
                ? incidencia.getEmpleadoAsignado().getNombre()
                : "Sin asignar";

        String mensaje = """
                🚨 <b>NUEVA INCIDENCIA</b>

                📌 <b>Folio:</b> %s
                📝 <b>Asunto:</b> %s
                ⚠️ <b>Prioridad:</b> %s
                👤 <b>Asignado:</b> %s
                📂 <b>Carpeta:</b> %s
                📍 <b>Estado:</b> %s

                🧾 <b>Descripción:</b>
                %s
                """.formatted(
                incidencia.getFolio(),
                incidencia.getAsunto(),
                incidencia.getPrioridad(),
                empleado,
                incidencia.getCarpetaOrigen(),
                incidencia.getEstado(),
                resumen(incidencia.getDescripcion())
        );

        telegramClient.sendMessage(mensaje);
    }

    @Override
    public void notificarIncidenciaResuelta(
            Incidencia incidencia,
            String usuario,
            String comentario) {

        String mensaje = """
                ✅ <b>INCIDENCIA RESUELTA</b>

                📌 <b>Folio:</b> %s
                👤 <b>Resuelta por:</b> %s
                📝 <b>Comentario:</b>
                %s
                """.formatted(
                incidencia.getFolio(),
                usuario,
                comentario
        );

        telegramClient.sendMessage(mensaje);
    }
    
    @Override
    public void notificarIncidenciaEnProceso(
            Incidencia incidencia,
            String usuario,
            String comentario) {

        String mensaje = """
                🔎 <b>INCIDENCIA EN PROCESO</b>

                📌 <b>Folio:</b> %s
                👤 <b>Tomada por:</b> %s
                📝 <b>Comentario:</b>
                %s
                """.formatted(
                incidencia.getFolio(),
                usuario,
                comentario
        );

        telegramClient.sendMessage(mensaje);
    }
    
    private String resumen(String texto) {
        if (texto == null || texto.isBlank()) {
            return "Sin descripción";
        }

        String limpio = texto
                .replaceAll("\\s+", " ")
                .trim();

        int max = 700;

        if (limpio.length() <= max) {
            return limpio;
        }

        return limpio.substring(0, max) + "...";
    }
}
