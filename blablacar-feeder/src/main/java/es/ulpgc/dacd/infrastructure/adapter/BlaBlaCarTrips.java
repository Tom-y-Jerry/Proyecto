package es.ulpgc.dacd.infrastructure.adapter;

import es.ulpgc.dacd.domain.model.Trip;
import es.ulpgc.dacd.domain.port.Trips;
import es.ulpgc.dacd.infrastructure.persistence.TripBuilder;

import java.util.List;

public final class BlaBlaCarTrips implements Trips {
    private final TripBuilder builder;

    public BlaBlaCarTrips(String dbUrl) {
        this.builder = new TripBuilder(dbUrl);
    }

    @Override
    public List<Trip> getCleanTrips() {
        return builder.buildTrips();
    }
}
