package es.ulpgc.dacd.ticketmasterfeeder.infrastructure.ports;
import es.ulpgc.dacd.ticketmasterfeeder.domain.Event;

public interface EventStorage {
    void save(Event event);
}
