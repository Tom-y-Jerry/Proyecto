package es.ulpgc.dacd.application.service;

import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Events;
import es.ulpgc.dacd.domain.port.EventsRepository;
import java.util.List;

public final class EventService {

    private final Events source;
    private final EventsRepository repo;

    public EventService(Events source, EventsRepository repo) {
        this.source = source;
        this.repo   = repo;
    }

    public void run() {
        List<Event> list = source.getCleanEvents();
        repo.saveAll(list);
    }
}
