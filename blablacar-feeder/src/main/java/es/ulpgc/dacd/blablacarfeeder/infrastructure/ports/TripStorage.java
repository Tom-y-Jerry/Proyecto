package es.ulpgc.dacd.blablacarfeeder.infrastructure.ports;
import es.ulpgc.dacd.blablacarfeeder.domain.Trip;

public interface TripStorage {
    void save(Trip trip);
}
