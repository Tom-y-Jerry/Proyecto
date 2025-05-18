package es.ulpgc.dacd.ticketmasterfeeder.infrastructure.ports;
import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;
import java.util.List;

public interface EventProvider {
    List<Event> provide();
}
