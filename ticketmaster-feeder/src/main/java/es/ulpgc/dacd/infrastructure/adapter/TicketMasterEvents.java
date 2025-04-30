package es.ulpgc.dacd.infrastructure.adapter;

import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Events;
import es.ulpgc.dacd.infrastructure.api.TicketMasterAPIClient;

import java.util.stream.Collectors;

import java.util.List;

public final class TicketMasterEvents implements Events {

    private final TicketMasterAPIClient client;
    private final TicketMasterParser parser = new TicketMasterParser();

    public TicketMasterEvents(TicketMasterAPIClient client) {
        this.client = client;
    }

    @Override
    public List<Event> getCleanEvents() {
        try {
            String json = client.fetchJson();
            List<Event> candidates = parser.parse(json);
            return deduplicate(candidates);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private List<Event> deduplicate(List<Event> list) {
        return list.stream()
                .collect(Collectors.toMap(Event::getId, e -> e, (a, b) -> a))
                .values().stream().toList();
    }
}

