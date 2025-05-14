package es.ulpgc.dacd;

import es.ulpgc.dacd.blablacarfeeder.Controller;
import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.ports.TripProvider;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.ports.TripStorage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    @Test
    public void shouldStoreAllProvidedTrips() {
        FakeTripProvider provider = new FakeTripProvider();
        FakeTripStorage storage = new FakeTripStorage();

        Controller controller = new Controller(provider, storage);
        controller.execute();

        assertEquals(2, storage.getStored().size());
        assertEquals("Las Palmas", storage.getStored().get(0).getOrigin());
        assertEquals("Madrid", storage.getStored().get(1).getDestination());
    }

    static class FakeTripProvider implements TripProvider {
        @Override
        public List<Trip> provide() {
            Trip t1 = new Trip("feeder-blablacar", "Las Palmas", "Tenerife",
                    Instant.parse("2025-05-06T10:00:00Z"),
                    Instant.parse("2025-05-06T13:00:00Z"),
                    25.0, "EUR");

            Trip t2 = new Trip("feeder-blablacar", "Tenerife", "Madrid",
                    Instant.parse("2025-05-07T08:00:00Z"),
                    Instant.parse("2025-05-07T14:00:00Z"),
                    50.0, "EUR");

            return List.of(t1, t2);
        }
    }

    static class FakeTripStorage implements TripStorage {
        private final List<Trip> stored = new ArrayList<>();

        @Override
        public void save(Trip trip) {
            stored.add(trip);
        }

        public List<Trip> getStored() {
            return stored;
        }
    }
}

