package es.ulpgc.dacd.ports;
import es.ulpgc.dacd.domain.Event;
import java.util.List;

public interface EventsProvider {
    List<Event> provide();
}
