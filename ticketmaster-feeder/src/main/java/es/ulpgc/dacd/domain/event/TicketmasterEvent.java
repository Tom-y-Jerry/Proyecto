package es.ulpgc.dacd.domain.event;

import es.ulpgc.dacd.domain.model.Event;
import java.time.Instant;

public final class TicketmasterEvent {

    public final String ts = Instant.now().toString();
    public final String ss;
    public final Event  data;

    public TicketmasterEvent(String ss, Event data) {
        this.ss   = ss;
        this.data = data;
    }
}