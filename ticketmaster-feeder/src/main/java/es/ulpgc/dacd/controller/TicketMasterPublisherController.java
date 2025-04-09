package es.ulpgc.dacd.controller;

import com.google.gson.Gson;
import es.ulpgc.dacd.domain.model.TicketmasterEvent;
import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.infrastructure.api.TicketMasterAPIClient;
import es.ulpgc.dacd.infrastructure.api.TicketMasterEvents;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;

import javax.jms.JMSException;
import java.util.List;

public class TicketMasterPublisherController {
    private final Gson gson = new Gson();
    private final TicketMasterEvents events;
    private final ActiveMQPublisher publisher;

    public TicketMasterPublisherController(TicketMasterAPIClient apiClient, ActiveMQPublisher publisher) {
        this.events = new TicketMasterEvents(apiClient);
        this.publisher = publisher;
    }

    public void run() {
        int count = 0;

        try {
            List<Event> cleanEvents = events.getCleanEvents();

            for (Event e : cleanEvents) {
                TicketmasterEvent event = new TicketmasterEvent(
                        "feeder-ticketmaster",
                        e.getId(),
                        e.getName(),
                        e.getDate(),
                        e.getCity()
                );

                String jsonEvent = gson.toJson(event);
                publisher.publish("prediction.Events", jsonEvent);
                count++;
            }

            System.out.println("✅ Se publicaron " + count + " eventos filtrados.");

        } catch (Exception e) {
            System.err.println("❌ Error al publicar eventos: " + e.getMessage());
        } finally {
            try {
                publisher.close();
            } catch (JMSException e) {
                System.err.println("❌ Error cerrando publisher: " + e.getMessage());
            }
        }
    }
}
