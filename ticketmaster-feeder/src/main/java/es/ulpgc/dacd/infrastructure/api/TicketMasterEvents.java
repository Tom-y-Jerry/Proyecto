package es.ulpgc.dacd.infrastructure.api;

import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Events;
import java.util.*;
import java.util.stream.Collectors;

public class TicketMasterEvents implements Events {

    private static final Set<String> POPULAR = Set.of(
            "Madrid","Barcelona","Valencia","Seville","Bilbao",
            "Zaragoza","Malaga","Granada","Vigo","A Coruña");

    private final TicketMasterAPIClient client;
    private final TicketMasterParser    parser = new TicketMasterParser();

    public TicketMasterEvents(TicketMasterAPIClient client) {
        this.client = client;
    }

    @Override
    public List<Event> getCleanEvents() {
        try {
            String json  = client.fetchJson();
            List<Event> candidates = parser.parse(json);
            return filterAndDedup(candidates);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private List<Event> filterAndDedup(List<Event> list) {
        return list.stream()
                .filter(e -> POPULAR.contains(e.getCity()))
                .collect(Collectors.toMap(Event::getId, e -> e, (a,b)->a))
                .values().stream().toList();
    }

}
