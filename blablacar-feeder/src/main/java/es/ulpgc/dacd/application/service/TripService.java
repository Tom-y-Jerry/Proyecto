package es.ulpgc.dacd.application.service;

import es.ulpgc.dacd.domain.model.Trip;
import es.ulpgc.dacd.domain.port.Trips;
import es.ulpgc.dacd.domain.port.TripsRepository;

import java.util.List;

public class TripService {
    private final Trips trips;
    private final TripsRepository repository;

    public TripService(Trips trips, TripsRepository repository) {
        this.trips = trips;
        this.repository = repository;
    }

    public void run() {
        List<Trip> tripList = fetchTrips();
        saveTrips(tripList);
    }

    private List<Trip> fetchTrips() {
        return trips.getCleanTrips();
    }

    private void saveTrips(List<Trip> trips) {
        repository.saveAll(trips);
    }
}
