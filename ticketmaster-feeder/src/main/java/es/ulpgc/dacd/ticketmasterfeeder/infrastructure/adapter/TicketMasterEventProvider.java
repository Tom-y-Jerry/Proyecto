package es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter;

import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.TicketMasterApiClient;
import es.ulpgc.dacd.ticketmasterfeeder.infrastructure.ports.EventProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterEventProvider implements EventProvider {
    private static final Logger log = LoggerFactory.getLogger(TicketMasterEventProvider.class);

    private final TicketMasterApiClient client;
    private final TicketMasterEventParser parser;

    public TicketMasterEventProvider(String eventsUrl, String apiKey) {
        this.client = new TicketMasterApiClient(eventsUrl, apiKey);
        this.parser = new TicketMasterEventParser();
    }

    @Override
    public List<Event> provide() {
        List<Event> allEvents = new ArrayList<>();
        try {
            List<String> jsonResponses = client.fetchEventsJson();
            for (String json : jsonResponses) {
                allEvents.addAll(parser.parseEvents(json));
            }
        } catch (Exception e) {
            log.error("Error providing events: {}", e.getMessage());
        }
        return allEvents;
    }
}
