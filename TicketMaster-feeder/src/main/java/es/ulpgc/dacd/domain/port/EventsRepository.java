package es.ulpgc.dacd.domain.port;
import es.ulpgc.dacd.domain.model.Event;
import java.util.List;

public interface EventsRepository {
    void save(Event event);
    void saveAll(List<Event> events);
    List<Event> findAll();
}
