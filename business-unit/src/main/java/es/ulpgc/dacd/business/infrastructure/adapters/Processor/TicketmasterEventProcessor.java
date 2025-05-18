package es.ulpgc.dacd.business.infrastructure.adapters.Processor;
import com.google.gson.*;
import es.ulpgc.dacd.business.infrastructure.ports.DatamartService;
import es.ulpgc.dacd.business.domain.Event;
import es.ulpgc.dacd.business.infrastructure.ports.EventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class TicketmasterEventProcessor implements EventProcessor<Event> {
    private static final Logger log = LoggerFactory.getLogger(TicketmasterEventProcessor.class);

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
            log.error("Error processing Ticketmaster event: {}", e.getMessage(), e);
        }
    }
}
