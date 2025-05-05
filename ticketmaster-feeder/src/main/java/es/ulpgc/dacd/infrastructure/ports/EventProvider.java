package es.ulpgc.dacd.infrastructure.ports;
import es.ulpgc.dacd.domain.Event;
import java.util.List;

public interface EventProvider {
    List<Event> provide();
}
