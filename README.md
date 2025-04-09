# Proyecto: Integración de APIs con Arquitectura Publisher/Subscriber

Este proyecto corresponde al Sprint 2 de la asignatura **Desarrollo de Aplicaciones para Ciencia de Datos**, en el que se amplía el sistema del Sprint 1 para incorporar un **sistema distribuido** basado en **ActiveMQ** y el patrón **Publisher/Subscriber**.

## 👥 Autoras

- Nombre Alumna 1
- Nombre Alumna 2

---

## 📦 Módulos del Proyecto

```
Proyecto/
├── blablacar-feeder/
├── ticketmaster-feeder/
├── event-store-builder/
└── eventstore/  ← generado automáticamente por el suscriptor
```

---

## 🔗 Tecnologías usadas

- Java 21
- ActiveMQ 5.15.12
- Gson 2.10
- SQLite JDBC
- IntelliJ IDEA

---

## ⚙️ Estructura General

| Módulo               | Rol                                                                                                                                 |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `blablacar-feeder`   | Conecta a la API de BlaBlaCar, filtra las paradas populares, y publica eventos JSON al topic `prediction.Stations`.                |
| `ticketmaster-feeder`| Conecta a la API de Ticketmaster, filtra los eventos relevantes, y publica eventos JSON al topic `prediction.Events`.              |
| `event-store-builder`| Se suscribe de forma duradera a los topics y guarda los eventos en archivos `.events`, organizados por fecha, fuente y tipo.       |

---

## 📤 Formato del Evento Publicado

```json
{
  "ts": "2025-04-11T12:00:00Z",
  "ss": "feeder-blablacar",
  "id": "ESMAD",
  "name": "Madrid",
  "latitude": 40.4168,
  "longitude": -3.7038
}
```

---

## 🚀 ¿Cómo ejecutar?

### 1. Lanzar ActiveMQ

Instalar desde [activemq.apache.org](https://activemq.apache.org/components/classic/download/)  
Y ejecutar:

```bash
# Windows
./bin/win64/activemq.bat start

# Linux/Mac
./bin/activemq start
```

---

### 2. Ejecutar el suscriptor (event-store-builder)

```bash
cd event-store-builder
# Ejecutar EventStoreBuilder.java
```

Esto dejará escuchando los eventos entrantes.

---

### 3. Ejecutar un feeder

- Para BlaBlaCar:

```bash
cd blablacar-feeder
# Ejecutar MainPublisher.java
```

- Para Ticketmaster:

```bash
cd ticketmaster-feeder
# Ejecutar MainPublisher.java
```

Cada uno publicará eventos filtrados al broker.

---

### 4. Ver los archivos generados

```bash
eventstore/
├── prediction.Stations/
│   └── feeder-blablacar/
│       └── 20250411.events
└── prediction.Events/
    └── feeder-ticketmaster/
        └── 20250411.events
```

---

## 📂 .env esperado por cada feeder

### BlaBlaCar:

```
BLABLACAR_API_KEY=tu_clave_aqui
BLABLACAR_API_URL=https://bus-api.blablacar.com/v3/stops
```

### Ticketmaster:

```
TICKETMASTER_API_KEY=tu_clave_aqui
TICKETMASTER_API_URL=https://app.ticketmaster.com/discovery/v2/events.json
```

---

## 🧪 Pruebas realizadas

- Se verificó que ambos feeders publican eventos filtrados al broker correctamente.
- El `event-store-builder` los guarda de forma organizada.
- Se han probado errores de red y conexión al broker con manejo adecuado de excepciones.

---

## ✅ Estado actual

- [x] Feeders funcionando
- [x] Eventos publicados a ActiveMQ
- [x] Suscripción duradera funcionando
- [x] Archivos `.events` organizados correctamente
- [x] Consola muestra eventos recibidos

---

---
