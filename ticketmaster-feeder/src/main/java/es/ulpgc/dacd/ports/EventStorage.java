package es.ulpgc.dacd.ports;
import es.ulpgc.dacd.domain.Event;
import java.util.List;

public interface EventStorage {
    void save(Event event);
}
