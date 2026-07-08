# SGIO - Sistema de Gestión de Incidencias Operativas

## 📋 Descripción

SGIO (Sistema de Gestión de Incidencias Operativas) es una plataforma desarrollada en Java con Spring Boot cuyo objetivo es automatizar el proceso de recepción, asignación, seguimiento y resolución de incidencias operativas.

Actualmente el sistema es capaz de:

- Procesar automáticamente incidencias provenientes de diferentes orígenes.
- Asignar incidencias de forma inteligente considerando disponibilidad del personal.
- Notificar eventos importantes mediante Telegram.
- Mantener un historial completo de cada incidencia.
- Procesar correos de forma automática mediante un Scheduler.
- Evitar el procesamiento duplicado de correos (Idempotencia).

El sistema fue diseñado utilizando una arquitectura desacoplada basada en eventos, permitiendo integrar fácilmente nuevas fuentes de información como Microsoft Outlook (Microsoft Graph), Gmail o cualquier otro proveedor.

---

# 🎯 Objetivo

Automatizar completamente el flujo de atención de incidencias operativas, reduciendo tiempos de respuesta, eliminando procesos manuales y mejorando el seguimiento de cada incidente.

---

# 🚀 Problema que resuelve

Antes del proyecto:

Correo Outlook
↓
Revisión Manual
↓
Registro Manual
↓
Asignación Manual
↓
Seguimiento Manual

Con SGIO:

Correo
↓
Procesamiento Automático
↓
Asignación Inteligente
↓
Notificación Telegram
↓
Seguimiento Automático

---

# 🏗 Arquitectura General

```
                 +----------------------+
                 |   Mail Scheduler     |
                 +----------------------+
                           |
                           v
                 +----------------------+
                 |     Mail Client      |
                 +----------------------+
                           |
                           v
                 +----------------------+
                 |    Mail Processor    |
                 +----------------------+
                           |
          +----------------+----------------+
          |                                 |
          v                                 v
+----------------------+        +----------------------+
|   Mail Validator     |        |  Mail Normalizer    |
+----------------------+        +----------------------+
                           |
                           v
                 +----------------------+
                 |  Incidencia Service  |
                 +----------------------+
                           |
                           v
                 +----------------------+
                 | Assignment Service   |
                 +----------------------+
                           |
                           v
                 +----------------------+
                 |    Spring Events     |
                 +----------------------+
                           |
                           v
                 +----------------------+
                 | Telegram Listener    |
                 +----------------------+
```

---

# 📂 Arquitectura del Proyecto

```
src
 ├── controller
 ├── dto
 ├── entity
 ├── enums
 ├── exception
 ├── integration
 │      └── mail
 │            ├── client
 │            ├── dto
 │            ├── entity
 │            ├── normalizer
 │            ├── processor
 │            ├── repository
 │            ├── scheduler
 │            ├── service
 │            └── validator
 ├── listener
 ├── mapper
 ├── notification
 ├── repository
 ├── schedule
 └── service
```

---

# ⚙️ Funcionalidades Implementadas

## Gestión de Empleados

- Alta
- Baja lógica
- Modificación
- Días laborales configurables
- Ausencias programadas

---

## Gestión de Incidencias

- Crear incidencia
- Consultar incidencia
- Cambiar estado
- Tomar incidencia
- Resolver incidencia
- Historial completo

Estados:

- PENDIENTE
- EN_PROCESO
- RESUELTA

---

## Motor de Asignación

El Assignment Engine considera:

- Empleado activo
- Días laborales
- Ausencias
- Última asignación
- Rotación automática

---

## Procesamiento de Correos

Actualmente existe un proveedor Mock que simula Outlook mediante archivos JSON.

Pipeline:

MailClient
↓
MailValidator
↓
MailNormalizer
↓
MailProcessor
↓
IncidenciaService

---

## Scheduler

El sistema revisa periódicamente la bandeja de entrada (actualmente Mock).

```
@Scheduled(...)
```

---

## Idempotencia

Cada correo procesado queda registrado evitando generar incidencias duplicadas.

Tabla:

```
correos_procesados
```

---

## Notificaciones

Actualmente se soporta:

- Telegram

Utilizando Spring Events.

Eventos disponibles:

- IncidenciaCreadaEvent
- IncidenciaEnProcesoEvent
- IncidenciaResueltaEvent

---

# 🧪 Pruebas

Actualmente existen pruebas unitarias para:

- AssignmentService
- MailProcessor

Frameworks utilizados:

- JUnit 5
- Mockito

Ejecutar:

```bash
mvn test
```

---

# 🛠 Tecnologías

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Web
- MySQL
- Hibernate
- Maven
- Lombok
- JUnit 5
- Mockito
- Telegram Bot API

---

# ▶️ Ejecución

Compilar:

```bash
mvn clean install
```

Ejecutar:

```bash
mvn spring-boot:run
```

---

# 📈 Roadmap

## Versión 1.0

- ✅ CRUD de incidencias
- ✅ CRUD de empleados
- ✅ Asignación automática
- ✅ Calendario laboral
- ✅ Ausencias
- ✅ Telegram
- ✅ Scheduler
- ✅ Mail Pipeline
- ✅ Idempotencia
- ✅ Pruebas unitarias

## Próximas funcionalidades

- 🔲 Microsoft Graph
- 🔲 Outlook Integration
- 🔲 Dashboard Web
- 🔲 Reportes
- 🔲 IA para clasificación automática de incidencias
- 🔲 IA para resumen de incidencias
- 🔲 Métricas y KPIs
- 🔲 API REST pública

---

# 👨‍💻 Autor

**César Eloy Chávez Luna**

Proyecto desarrollado como iniciativa para automatizar el proceso de gestión de incidencias operativas mediante una arquitectura basada en Spring Boot, eventos y procesamiento desacoplado.

---

# 📄 Licencia

Proyecto de uso interno.

Todos los derechos reservados.