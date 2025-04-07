# Proyecto: Captura de Datos desde APIs Externas

Este proyecto forma parte del Sprint 1 de la asignatura **Desarrollo de Aplicaciones para Ciencia de Datos** (Grado en Ingeniería en Ciencia de Datos - ULPGC).

El objetivo es capturar datos dinámicos desde dos APIs externas y almacenarlos periódicamente en una base de datos SQLite usando Java con una arquitectura hexagonal.

## 📦 Estructura del Proyecto

```
├── src/
│   ├── controller/
│   ├── domain/
│   │   └── port/
│   ├── infrastructure/
│   │   ├── adapter/
│   │   └── api/
│   └── Main.java
├── data.db
├── .env
└── README.md
```

## 🔗 APIs utilizadas

- **Ticketmaster API**: proporciona información sobre eventos en España.
- **BlaBlaCar API**: obtiene información sobre paradas de autobuses.

## ⚙️ Tecnologías

- Java 21
- IntelliJ IDEA
- SQLite (usando `sqlite-jdbc`)
- OkHttp (para consumo de APIs)
- Dotenv (para gestionar variables de entorno)

## 🛠️ Configuración del entorno

1. Crea un archivo `.env` en la raíz del proyecto con el siguiente contenido:

```
TICKETMASTER_API_KEY=tu_clave_ticketmaster
TICKETMASTER_API_URL=https://app.ticketmaster.com/discovery/v2/events.json

BLABLACAR_API_KEY=tu_clave_blablacar
BLABLACAR_API_URL=https://bus-api.blablacar.com/v3/stops
```

2. Asegúrate de tener la base de datos SQLite en el archivo `data.db` o se generará automáticamente.

## ▶️ Ejecución

Cada API tiene su clase `Main` que se encarga de ejecutar periódicamente la consulta y almacenamiento:

- **Ticketmaster:**
  ```bash
  Ejecuta Main.java en el módulo de eventos.
  ```

- **BlaBlaCar:**
  ```bash
  Ejecuta Main.java en el módulo de estaciones.
  ```

Ambos usan `ScheduledExecutorService` para realizar consultas **cada hora**.

## 📄 Estado actual

- [x] API Clients funcionando
- [x] Periodicidad implementada
- [x] Variables en `.env`
- [x] Persistencia en SQLite
- [x] Arquitectura hexagonal

## ✍️ Autores

- Carlota Ayala
- Lucía Cruz
