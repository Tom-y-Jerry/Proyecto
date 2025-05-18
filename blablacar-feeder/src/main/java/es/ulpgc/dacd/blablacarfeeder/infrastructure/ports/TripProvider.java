package es.ulpgc.dacd.blablacarfeeder.infrastructure.ports;
import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import java.util.List;

public interface TripProvider {
    List<Trip> provide();
}
