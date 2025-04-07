package es.ulpgc.dacd.controller;

import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Events;
import es.ulpgc.dacd.domain.port.EventsRepository;
import java.util.List;

public class TicketMasterServiceController {
    private final Events events;
    private final EventsRepository repository;

    public TicketMasterServiceController(Events events, EventsRepository repository) {
        this.events = events;
        this.repository = repository;
    }

    public void run() {
        System.out.println("Ejecutando TicketMasterService...");
        List<Event> cleanEvents = events.getCleanEvents();
        System.out.println("Eventos obtenidas: " + cleanEvents.size());
        repository.saveAll(cleanEvents);
        System.out.println("Datos de eventos insertados correctamente.");
    }
}