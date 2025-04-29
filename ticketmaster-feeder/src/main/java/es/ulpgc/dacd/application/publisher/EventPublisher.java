package es.ulpgc.dacd.application.publisher;

import com.google.gson.Gson;
import es.ulpgc.dacd.domain.event.TicketmasterEvent;
import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.infrastructure.api.TicketMasterEvents;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;
import java.util.List;

public class EventPublisher {

    private final TicketMasterEvents source;
    private final ActiveMQPublisher  broker;
    private final Gson gson = new Gson();

    public EventPublisher(TicketMasterEvents source, ActiveMQPublisher broker) {
        this.source = source;
        this.broker = broker;
    }

    public void run() {
        source.getCleanEvents().forEach(this::publish);
    }

    private void publish(Event ev) {
        try {
            String json = gson.toJson(new TicketmasterEvent("feeder-ticketmaster", ev));
            broker.publish("prediction.Events", json);
        } catch (Exception e) {
            System.err.println("❌ Publish error: " + e.getMessage());
        }
    }
}
