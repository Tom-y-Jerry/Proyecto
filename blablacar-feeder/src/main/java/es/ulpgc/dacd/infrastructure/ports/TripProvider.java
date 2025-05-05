package es.ulpgc.dacd.infrastructure.ports;

import es.ulpgc.dacd.domain.Trip;
import java.util.List;

public interface TripProvider {
    List<Trip> provide();
}
