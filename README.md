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
| Carlota Ayala Pérez | [@carlotaayala](https://github.com/carlotaayala) |
| Lucía Cruz Toste  | [@luciacruz](https://github.com/luciacruz) |

---

## 📑 Índice
1. [Propuesta de valor](#-propuesta-de-valor)  
2. [Funcionalidades](#-funcionalidades)  
3. [Módulos del proyecto](#-módulos-del-proyecto)  
4. [Justificación de las APIs y del datamart](#-justificación-de-las-apis-y-del-datamart)  
5. [Requisitos previos](#-requisitos-previos)  
6. [Instalación y compilación](#-instalación-y-compilación)  
7. [Variables de entorno](#-variables-de-entorno)  
8. [Cómo ejecutar](#-cómo-ejecutar)  
9. [Flujo de la GUI paso a paso](#-flujo-de-la-gui-paso-a-paso)  
10. [Estructura de archivos generados](#-estructura-de-archivos-generados)  
11. [Tecnologías](#-tecnologías)  
12. [Tests](#-tests)  

---

## 💡 Propuesta de valor
- **Planificación integral** → une eventos y transporte en una sola interfaz.  
- **Datos en vivo** → feeders consultan las APIs cada X minutos; el usuario ve disponibilidad real.  
- **Extensible** → para añadir otra fuente basta un nuevo adapter y topic.  
- **Demostrativo** → muestra patrones y buenas prácticas en Java 21.

---
## 🧠 Funcionalidades
- 🔎 **Obtención de eventos** culturales mediante la API de Ticketmaster.  
- 🚌 **Obtención de trayectos** y tarifas mediante la API de BlaBlaCar.  
- 📨 **Publicación** de ambos flujos como mensajes JSON en ActiveMQ (topics `Events` y `Trips`).  
- 💾 **Persistencia** de todos los mensajes en archivos `.events` y en una base de datos SQLite integrada.  
- 🖥️ **Visualización** de datos históricos gracias a una GUI Java Swing.

---

## 📦 Módulos del proyecto
| Módulo | Patrón | Responsabilidad |
|--------|--------|-----------------|
| `blablacar-feeder` | Adapter + Publisher | Publica viajes en topic **Trips** |
| `ticketmaster-feeder` | Adapter + Publisher | Publica eventos en topic **Events** |
| `event-store-builder` | Consumer | Registra mensajes en `event-store/*.events` |
| `business-unit` | Consumer + GUI | Persiste en SQLite y muestra interfaz Swing |

---

## 🔎 Justificación de las APIs y del datamart
- **BlaBlaCar** → rutas económicas, populares entre estudiantes.  
- **Ticketmaster** → gran catálogo cultural, API bien documentada.  

---

## ⚙️ Requisitos previos
- Java 21  
- Apache Maven 3.6+  
- ActiveMQ 5.17+ (`tcp://localhost:61616`)  
- Conexión a Internet

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
| `BLABLACAR_API_KEY` | Token BlaBlaCar | `abc123` |
| `TICKETMASTER_API_KEY` | Token Ticketmaster | `xyz789` |
| `ACTIVEMQ_URL` | URL broker | `tcp://localhost:61616` |

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

### 2. Event Store
```bash
cd event-store-builder
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.eventstorebuilder.Main ^
 -Dexec.args="tcp://localhost:61616 event-store-builder/Trips event-store-builder/Events"
```

### 3. Feeders
```bash
# BlaBlaCar
cd blablacar-feeder
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.blablacarfeeder.Main ^
 -Dexec.args="https://bus-api.blablacar.com/v3/stops https://bus-api.blablacar.com/v3/fares $BLABLACAR_API_KEY tcp://localhost:61616"

# Ticketmaster
cd ticketmaster-feeder
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.ticketmasterfeeder.Main ^
 -Dexec.args="https://app.ticketmaster.com/discovery/v2/events.json $TICKETMASTER_API_KEY tcp://localhost:61616"
```

### 4. Business Unit (processor + GUI)
```bash
cd business-unit
# Persistencia en SQLite
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.business.Controller ^
 -Dexec.args="tcp://localhost:61616 datamart.db"

# GUI
mvn exec:java -Dexec.mainClass=es.ulpgc.dacd.business.EventViewerGUI ^
 -Dexec.args="datamart.db"
```

---

## 🖥️ Flujo de la GUI paso a paso

1. **Seleccionar origen**  
   - Desplegable con todas las ciudades de salida disponibles.  
2. **Explorar eventos**  
   - Se listan todos los eventos obtenidos vía Ticketmaster.  
3. **Elegir evento**  
   - Al hacer clic en un evento se activan las rutas asociadas.  
4. **Ver trayectos recomendados**  
   - Tabla con precio, hora de salida y plazas libres para llegar al evento.  

Con tres clics el usuario descubre un evento y elige la opción de viaje más económica.

---

## 🗃 Estructura de archivos generados
```
event-store-builder/
└── Trips/ | Events/
    └── feeder-*/YYYYMMDD.events

business-unit/
└── datamart.db
```
> Cada línea de un `.events` es un objeto JSON serializado.

---

## 🧩 Principios y patrones por módulo
| Módulo | Patrones | Principios |
|--------|----------|-----------|
| Feeders | Adapter + Publisher | SRP, inmutabilidad |
| Event Store | Consumer + Event Sourcing | Open/Closed |
| Business Unit | Facade + MVC | DAO, DRY |

---

## 🛠️ Tecnologías
Java 21 · Maven · ActiveMQ · SQLite · Swing · Gson

---

## 🧪 Tests

```bash
mvn test
```
Se ejecutan tests unitarios (JUnit) en cada módulo.


