package es.ulpgc.dacd.domain.port;
import es.ulpgc.dacd.domain.model.Event;
import java.util.List;

public interface Events {
    List<Event> getCleanEvents();
}
