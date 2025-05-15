package es.ulpgc.dacd.ticketmasterfeeder;

import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.ports.EventProvider;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.ports.EventStorage;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {

    private final EventProvider eventProvider;
    private final EventStorage storage;

    public Controller(EventProvider eventProvider, EventStorage storage) {
        this.eventProvider = eventProvider;
        this.storage = storage;
    }

    public void execute() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            List<Event> events = eventProvider.provide();
            for (Event event : events) {
                storage.save(event);
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}