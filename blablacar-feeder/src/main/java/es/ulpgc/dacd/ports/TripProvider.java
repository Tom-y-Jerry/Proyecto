package es.ulpgc.dacd.ports;

import es.ulpgc.dacd.domain.Trip;
import java.util.List;

public interface TripProvider {
    List<Trip> provide();
}
