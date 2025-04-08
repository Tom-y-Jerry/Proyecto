package es.ulpgc.dacd.controller;

import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Subscriber;
import es.ulpgc.dacd.domain.port.Publisher;
import es.ulpgc.dacd.infrastructure.api.TicketMasterAPIClient;
import es.ulpgc.dacd.infrastructure.api.TicketMasterEvents;

import java.util.List;

public class TicketmasterFeederService {
    private final Publisher publisher;
    private final Subscriber store;

    public TicketmasterFeederService(Publisher publisher, Subscriber store) {
        this.publisher = publisher;
        this.store = store;
    }

    public void run() {
        try {
            TicketMasterAPIClient apiClient = new TicketMasterAPIClient();
            TicketMasterEvents events = new TicketMasterEvents(apiClient);

            List<Event> listEvents = events.getCleanEvents();

            for (Event e : listEvents) {
                Event event = new Event(
                        e.getId(), e.getName(), e.getDate(), e.getCity()
                );
                publisher.publish(event);
                store.store(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

