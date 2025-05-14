package es.ulpgc.dacd.business.infrastructure.adapters;

import com.google.gson.*;
import es.ulpgc.dacd.business.infrastructure.ports.DatamartService;
import es.ulpgc.dacd.business.domain.Event;

import java.time.Instant;

public class TicketmasterEventProcessor implements EventProcessor<Event> {
    private final DatamartService datamart;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, type, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    public TicketmasterEventProcessor(DatamartService datamart) {
        this.datamart = datamart;
    }

    @Override
    public void process(Event event) {
        try {
            datamart.insertEvent(
                    event.getId(),
                    event.getName(),
                    event.getDate().toString(),
                    event.getTime(),
                    event.getCity(),
                    event.getSs(),
                    gson.toJson(event)
            );
        } catch (Exception e) {
            throw new RuntimeException("Error processing Ticketmaster event", e);
        }
    }
}
