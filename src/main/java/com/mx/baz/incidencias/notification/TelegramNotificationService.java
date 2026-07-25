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
                🏢 <b>Sucursal:</b> %s
                👤 <b>Cliente:</b> %s
                🆔 <b>Cliente único:</b> %s
                💻 <b>Equipo:</b> %s
                ⚠️ <b>Motivo:</b> %s

                👨‍💻 <b>Asignado:</b> %s
                📍 <b>Estado:</b> %s
                """.formatted(
                html(valor(incidencia.getFolio())),
                html(valor(incidencia.getAsunto())),
                html(valor(incidencia.getSucursal())),
                html(valor(incidencia.getNombreCliente())),
                html(valor(incidencia.getClienteUnico())),
                html(valor(incidencia.getEquipo())),
                html(resumen(valor(incidencia.getMotivo()), 500)),
                html(valor(incidencia.getEmpleadoAsignado().getNombre())),
                html(valor(incidencia.getEstado()))
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
    private String valor(Object valor) {
        return valor == null || valor.toString().isBlank()
                ? "No identificado"
                : valor.toString();
    }

    private String resumen(String texto, int maximo) {
        String limpio = texto.replaceAll("\\s+", " ").trim();

        if (limpio.length() <= maximo) {
            return limpio;
        }

        return limpio.substring(0, maximo) + "...";
    }

    private String html(String texto) {
        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
    
    public void notificarIncidenciaReabierta(
            Incidencia incidencia,
            String motivoReapertura) {

        String mensaje = """
                🔄 INCIDENCIA REABIERTA

                Folio:
                %s

                Sucursal:
                %s

                Cliente:
                %s

                Cliente único:
                %s

                Equipo:
                %s

                Motivo:
                %s

                Estado:
                %s

                ⚠️ Se requiere atención.
                """.formatted(
                valorSeguro(incidencia.getFolio()),
                valorSeguro(incidencia.getSucursal()),
                valorSeguro(incidencia.getNombreCliente()),
                valorSeguro(incidencia.getClienteUnico()),
                valorSeguro(incidencia.getEquipo()),
                valorSeguro(motivoReapertura),
                valorSeguro(
                        incidencia.getEstado() != null
                                ? incidencia.getEstado().name()
                                : null
                )
        );

        telegramClient.sendMessage(mensaje);
        
    }
    private String valorSeguro(String valor) {

        return valor == null || valor.isBlank()
                ? "No disponible"
                : valor;
    }
}
