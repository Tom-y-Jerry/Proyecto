# 🎟️ Event&Go

> **Event&Go** es una plataforma modular que **consume datos en tiempo real** desde las APIs públicas de  
> **BlaBlaCar** (trayectos) y **Ticketmaster** (eventos culturales).  
> El proyecto muestra **cómo diseñar, desarrollar y orquestar código Java 21** siguiendo principios  
> de Clean Code y Arquitectura Hexagonal, publicando la información en ActiveMQ,  
> almacenándola en ficheros y en SQLite, y presentándola a través de una GUI Java Swing.

---

## 👥 Autoras

| Nombre | GitHub |
|--------|--------|
| Carlota Ayala | [@carlotaayala](https://github.com/carlotaayala) |
| Lucía Cruz    | [@luciacruz](https://github.com/luciacruz) |

---

## 📑 Índice
1. [Funcionalidades](#-funcionalidades)  
2. [Arquitectura](#-arquitectura)  
3. [Módulos](#-módulos-del-proyecto)  
4. [Requisitos](#-requisitos-previos)  
5. [Instalación](#-instalación-y-compilación)  
6. [Variables de entorno](#-variables-de-entorno)  
7. [Ejecución](#-cómo-ejecutar)  
8. [Estructura de archivos](#-estructura-de-archivos-generados)  
9. [Tecnologías](#-tecnologías)  
10. [Pruebas](#-tests)  
11. [Contribuir](#-contribuir)  
12. [Licencia](#-licencia)

---

## 🧠 Funcionalidades
- 🔎 **Obtención de eventos** culturales mediante la API de Ticketmaster.  
- 🚌 **Obtención de trayectos** y tarifas mediante la API de BlaBlaCar.  
- 📨 **Publicación** de ambos flujos como mensajes JSON en ActiveMQ (topics `Events` y `Trips`).  
- 💾 **Persistencia** de todos los mensajes en archivos `.events` y en una base de datos SQLite integrada.  
- 🖥️ **Visualización** de datos históricos gracias a una GUI Java Swing.

---

## 🧱 Arquitectura

- **Arquitectura Hexagonal (Ports & Adapters)**.  
- **Clean Code / LAMDA**: métodos ≤ 10 líneas y clases con responsabilidad única.  
- Separación total entre dominio e infraestructura.

---

## 📦 Módulos del Proyecto

| Módulo                | Descripción (responsabilidad única)                   |
|-----------------------|-------------------------------------------------------|
| **blablacar-feeder**  | Lee viajes de BlaBlaCar → publica en topic **Trips**  |
| **ticketmaster-feeder**| Lee eventos de Ticketmaster → publica en topic **Events** |
| **event-store-builder**| Suscriptor duradero de `Trips` y `Events` → guarda en archivos |
| **business-unit**     | Consume mensajes, persiste en SQLite y lanza la GUI   |

---

## ⚙️ Requisitos Previos

- **Java 21**  
- **Apache Maven 3.6+**  
- **ActiveMQ** corriendo en `tcp://localhost:61616`  
- Conexión a Internet (para las APIs)  
- **SQLite** embebido (no requiere instalación)

---

## 🛠 Instalación y Compilación

```bash
git clone https://github.com/tu-usuario/event-and-go.git
cd event-and-go
mvn clean install
```

---

## 🌍 Variables de entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `BLABLACAR_API_KEY` | Token de la API de BlaBlaCar | `abc123` |
| `BLABLACAR_API_URL` | Endpoint de paradas | `https://bus-api.blablacar.com/v3/stops` |
| `BLABLACAR_API_FARES` | Endpoint de tarifas | `https://bus-api.blablacar.com/v3/fares` |
| `TICKETMASTER_API_KEY` | Token de Ticketmaster | `xyz789` |
| `TICKETMASTER_API_URL` | Endpoint de eventos | `https://app.ticketmaster.com/discovery/v2/events.json` |
| `ACTIVEMQ_URL` | URL del broker ActiveMQ | `tcp://localhost:61616` |

Se pueden suministrar vía `application.properties`, `.env` o como argumentos de línea.

---

## 📦 Formatos de mensajes publicados

### Evento BlaBlaCar (`Trips`)

```json
{
  "ts": "2025-05-17T10:00:00Z",
  "ss": "feeder-blablacar",
  "departure_place": "Madrid",
  "arrival_place": "Barcelona",
  "departure_time": "2025-05-21T15:30:00Z",
  "price": 22.5,
  "seats": 2
}
```

### Evento Ticketmaster (`Events`)

```json
{
  "ts": "2025-05-17T10:15:00Z",
  "ss": "feeder-ticketmaster",
  "id": "XKCD1234",
  "name": "Concierto Coldplay",
  "date": "2025-05-25",
  "time": "20:00",
  "city": "Valencia"
}
```

---

## 🚀 Cómo ejecutar

### 1. Iniciar ActiveMQ

Descarga en <https://activemq.apache.org/components/classic/download/>

Windows:
```
./bin/win64/activemq.bat start
```
Linux / macOS:
```
./bin/activemq start
```
### Verificar que está activo

Abrir un navegador y entrar en: <http://localhost:8161/>
>(Si es la primera vez, usuario admin / contraseña admin).
---

### 2. Arrancar **event-store-builder**

```
cd event-store-builder
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.eventstorebuilder.Main \
 -Dexec.args="tcp://localhost:61616 event-store-builder/Trips event-store-builder/Events"
```

---

### 3. Arrancar los feeders

#### BlaBlaCar Feeder
```
cd blablacar-feeder
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.blablacarfeeder.Main \
 -Dexec.args="https://bus-api.blablacar.com/v3/stops https://bus-api.blablacar.com/v3/fares $BLABLACAR_API_KEY tcp://localhost:61616"
```

#### Ticketmaster Feeder
```
cd ticketmaster-feeder
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.ticketmasterfeeder.Main \
 -Dexec.args="https://app.ticketmaster.com/discovery/v2/events.json $TICKETMASTER_API_KEY tcp://localhost:61616"
```

---

### 4. Arrancar **business-unit**

Procesador SQLite:
```
cd business-unit
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.business.Controller \
 -Dexec.args="tcp://localhost:61616 datamart.db"
```

GUI:
```
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.business.EventViewerGUI \
 -Dexec.args="datamart.db"
```

---

## 🗃 Estructura de archivos generados

```
event-store-builder/
├── Trips/
│   └── feeder-blablacar/
│       └── YYYYMMDD.events
└── Events/
    └── feeder-ticketmaster/
        └── YYYYMMDD.events

business-unit/
└── datamart.db
```
> Cada línea de un `.events` es un objeto JSON serializado.

---

## 🛠️ Tecnologías

- Java 21 · Maven  
- ActiveMQ  
- SQLite (embebido)  
- Java Swing  
- GSON  

---

## 🧪 Tests

```bash
mvn test
```
Se ejecutan tests unitarios (JUnit) en cada módulo.


