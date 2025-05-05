package es.ulpgc.dacd.infrastructure.ports;

import es.ulpgc.dacd.domain.Trip;

public interface TripStorage {
    void save(Trip trip);
}
