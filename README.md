# Event&Go

> **Event&Go** es una plataforma modular que **consume datos en tiempo real** desde las APIs públicas de  
> **BlaBlaCar** (trayectos) y **Ticketmaster** (eventos culturales).  
> El proyecto muestra **cómo diseñar, desarrollar y orquestar código Java 21** siguiendo principios  
> de Clean Code y Arquitectura Hexagonal, publicando la información en ActiveMQ,  
> almacenándola en ficheros y en SQLite, y presentándola a través de una GUI Java Swing.

---

## Autoras

| Nombre | GitHub                                           |
|--------|--------------------------------------------------|
| Carlota Ayala Pérez | [@carlotaayala](https://github.com/carlotaayala) |
| Lucía Cruz Toste  | [@l-cruz](https://github.com/l-cruz)             |

---

## Índice
1. [Propuesta de valor](#propuesta-de-valor)
2. [Funcionalidades](#funcionalidades)
3. [Arquitectura del sistema y de la aplicación](#arquitectura-del-sistema-y-de-la-aplicación)
4. [Módulos del proyecto](#módulos-del-proyecto)
5. [Justificación de las APIs y del datamart](#justificación-de-las-apis-y-del-datamart)
6. [Requisitos previos](#requisitos-previos)
7. [Instalación y compilación](#instalación-y-compilación)
8. [Variables de entorno](#variables-de-entorno)
9. [Cómo ejecutar](#cómo-ejecutar-el-proyecto)
10. [Flujo de la GUI paso a paso](#flujo-de-la-gui-paso-a-paso)
11. [Estructura de archivos generados](#estructura-de-archivos-generados)
12. [Tecnologías](#tecnologías)
13. [Tests](#tests)


---

## Propuesta de valor
- **Planificación integral** → une los eventos y el transporte en una sola interfaz.  
- **Datos en vivo** → los feeders consultan las APIs cada X tiempo; el usuario ve disponibilidad en tiempo real.  
- **Extensible** → para añadir otra fuente de información es suficiente con un nuevo adapter y topic.  
- **Demostrativo** → muestra patrones y buenas prácticas en Java 21.

---
## Funcionalidades
- **Obtención de eventos** culturales y de ocio a través la API de Ticketmaster.  
- **Obtención de trayectos** y tarifas asequibles a través la API de BlaBlaCar.  
- **Publicación** de ambos flujos como mensajes JSON en ActiveMQ (topics `Events` y `Trips`).  
- **Persistencia** de todos los mensajes en archivos `.events` y en una base de datos SQLite integrada.  
- **Visualización** de datos históricos gracias a una GUI Java Swing.

---

## Arquitectura del sistema y de la aplicación

El sistema sigue una arquitectura modular basada en principios de Clean Code y Arquitectura Hexagonal:

- **Feeders**: consumen las APIs externas (BlaBlaCar y Ticketmaster), procesan los datos y los publican como mensajes JSON en ActiveMQ.
- **Event Store**: escucha los mensajes y los almacena en ficheros `.events`.
- **Business Unit**: esta unidad tiene una lógica inspirada en la **arquitectura lambda**, combinando flujos en tiempo real (mensajes de ActiveMQ) con datos históricos (archivos `.events` y registros SQLite), proporcionando una vista completa e integrada para el usuario mediante la interfaz.

Cada módulo es independiente, se comunican a través del broker de mensajes, y pueden evolucionar o escalar sin afectar a los demás.


---

## Módulos del proyecto
| Módulo | Patrón | Responsabilidad |
|--------|--------|-----------------|
| `blablacar-feeder` | Adapter + Publisher | Publica viajes en topic **Trips** |
| `ticketmaster-feeder` | Adapter + Publisher | Publica eventos en topic **Events** |
| `event-store-builder` | Consumer | Registra mensajes en `event-store/*.events` |
| `business-unit` | Consumer + GUI | Persiste en SQLite y muestra interfaz Swing |

---

## Justificación de las APIs y del datamart
- **BlaBlaCar** → rutas económicas, populares entre estudiantes.  
- **Ticketmaster** → gran catálogo cultural, API bien documentada.  

---

## Requisitos previos
- Java 21  
- Apache Maven 3.6+  
- ActiveMQ 5.17.6 (`tcp://localhost:61616`)  
- Conexión a Internet

---

## Instalación y Compilación

```bash
git clone https://github.com/Tom-y-Jerry/Proyecto/tree/master
cd Proyecto
mvn clean install
```

---

## Variables de entorno
| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `BLABLACAR_API_KEY` | Token BlaBlaCar | `abc123` |
| `TICKETMASTER_API_KEY` | Token Ticketmaster | `xyz789` |
| `ACTIVEMQ_URL` | URL broker | `tcp://localhost:XXXXX` |
|`DB_PATH` | URL database | `ejemplo.db` |
|`URL_API` | URL API | `https://url_api/` |
|`TOPIC_NAME` | Topic | `Ejemplo` |


---

## Formatos de mensajes publicados

### Evento BlaBlaCar (`Trips`)

```json
{
  "ts": "2025-05-17T10:00:00Z",
  "ss": "feeder-blablacar",
  "departure_place": "Madrid",
  "arrival_place": "Barcelona",
  "departure_time": "2025-05-21T15:30:00Z",
  "arrival_time": "2025-05-22T15:30:00Z",
  "price": 22.5,
  "currency": "EUR"
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

## Cómo ejecutar el proyecto

### 1. Iniciar ActiveMQ

Descarga el .zip en <https://activemq.apache.org/components/classic/download/classic-05-17-06> y para ejecutarlo dependiendo tu sistema se ejecuta con una instrucción diferente.

Antes de ejecutar esta instrucción desde la consola tienes que estar dentro de la carpeta.

Windows:
```
bin\activemq start
```
Linux / macOS:
```
./activemq start
```
### Verificar que está activo

Abrir un navegador y entrar en: <http://localhost:8161/>
>(Para inciar sesión: usuario admin / contraseña admin).
---

### 2. Event Store
```bash
argumentos requeridos en event-store-builder ="tcp://localhost:61616 eventstore Trips Events"
```

### 3. Feeders
```bash
argumentos requeridos en blablacar ="https://bus-api.blablacar.com/v3/stops https://bus-api.blablacar.com/v3/fares $BLABLACAR_API_KEY tcp://localhost:61616"
 
argumentos requeridos en ticketmaster ="https://app.ticketmaster.com/discovery/v2/events.json $TICKETMASTER_API_KEY tcp://localhost:61616"
```

### 4. Business Unit (processor + GUI)
```bash
argumentos requeridos="tcp://localhost:61616 datamart.db"
```

---

## Flujo de la GUI paso a paso

1. **Seleccionar origen**  
   - Desplegable con todas las ciudades de salida disponibles.  
2. **Explorar eventos**  
   - Se listan todos los eventos obtenidos vía Ticketmaster.  
3. **Elegir evento**  
   - Al hacer clic en un evento se activan las rutas asociadas.  
4. **Ver trayectos recomendados**  
   - Tabla con precio, hora de salida, de llegada y totales para llegar al evento.  

Con tres clics el usuario descubre un evento y elige la opción de viaje más económica.

---

## Estructura de archivos generados
```
event-store-builder/
└── Trips/ | Events/
    └── feeder-*/YYYYMMDD.events

business-unit/
datamart.db
```
> Cada línea de un `.events` es un objeto JSON serializado.

---

## Principios y patrones por módulo
| Módulo | Patrones | Principios                      |
|--------|----------|---------------------------------|
| Feeders | Adapter, Publisher (eventos con ActiveMQ) | SRP, inmutabilidad, Open/Closed |
| Event Store | Consumer, Event Sourcing (almacenamiento en fichero) | Open/Closed, SRP                |
| Business Unit | Facade (controladores), MVC (GUI) | DRY, SRP                   |

---

## Tecnologías
Java 21 · Maven · ActiveMQ · SQLite · Swing · Gson · Git

---

## Tests

```bash
mvn test
```
Se ejecutan tests unitarios (JUnit) en cada módulo.

