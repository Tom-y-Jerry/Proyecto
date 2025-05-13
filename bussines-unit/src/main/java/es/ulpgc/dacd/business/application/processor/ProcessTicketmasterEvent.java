package es.ulpgc.dacd.business.application.processor;

import com.google.gson.*;
import es.ulpgc.dacd.business.application.service.DatamartService;
import es.ulpgc.dacd.business.domain.model.Event;

import java.time.Instant;

public class ProcessTicketmasterEvent implements EventProcessor<Event> {
    private final DatamartService datamart;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, type, context) ->
                    new JsonPrimitive(src.toString()))
            .create();

    public ProcessTicketmasterEvent(DatamartService datamart) {
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
