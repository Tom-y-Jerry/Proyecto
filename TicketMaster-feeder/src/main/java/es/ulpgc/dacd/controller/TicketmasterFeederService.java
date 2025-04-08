package es.ulpgc.dacd.controller;

import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Publisher;
import es.ulpgc.dacd.infrastructure.api.TicketMasterAPIClient;
import es.ulpgc.dacd.infrastructure.api.TicketMasterEvents;

import java.util.List;

public class TicketmasterFeederService {
    private final Publisher publisher;

    public TicketmasterFeederService(Publisher publisher) {
        this.publisher = publisher;
    }

    public void run() {
        try {
            TicketMasterAPIClient apiClient = new TicketMasterAPIClient();
            TicketMasterEvents events = new TicketMasterEvents(apiClient);

            List<Event> listEvents = events.getCleanEvents();

            for (Event event : listEvents) {
                publisher.publish(event);
            }

            System.out.println("✅ Eventos publicados al broker correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
