package es.ulpgc.dacd.infrastructure.ports;
import es.ulpgc.dacd.domain.Event;

public interface EventStorage {
    void save(Event event);
}
