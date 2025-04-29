package es.ulpgc.dacd.domain.port;

import es.ulpgc.dacd.domain.model.Trip;

import java.util.List;

public interface TripsRepository {
    void save(Trip trip);
    void saveAll(List<Trip> trip);
    List<Trip> findAll();
}
