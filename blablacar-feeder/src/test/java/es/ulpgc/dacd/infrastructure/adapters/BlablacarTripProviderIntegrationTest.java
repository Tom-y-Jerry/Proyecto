package es.ulpgc.dacd.infrastructure.adapters;

import com.google.gson.JsonObject;
import es.ulpgc.dacd.domain.Trip;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BlablacarTripProviderIntegrationTest {

    @Test
    public void shouldReturnTripsWithFakeApi() {
        BlablacarTripProvider provider = new FakeBlablacarTripProvider();
        List<Trip> trips = provider.provide();

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);
        assertEquals("Las Palmas", trip.getOrigin());
        assertEquals("Tenerife", trip.getDestination());
        assertEquals(30.0, trip.getPrice(), 0.01);
        assertEquals("EUR", trip.getCurrency());
    }

    private static class FakeBlablacarTripProvider extends BlablacarTripProvider {
        public FakeBlablacarTripProvider() {
            super("fakeUrl1", "fakeUrl2", "fakeKey", 1);
        }

        @Override
        public List<Trip> provide() {
            JsonObject fare = new JsonObject();
            fare.addProperty("departure", "2025-05-06T10:00:00Z");
            fare.addProperty("arrival", "2025-05-06T13:00:00Z");
            fare.addProperty("price_cents", 3000);
            fare.addProperty("price_currency", "EUR");

            Trip trip = new Trip(
                    "feeder-blablacar", "Las Palmas", "Tenerife",
                    Instant.parse(fare.get("departure").getAsString()),
                    Instant.parse(fare.get("arrival").getAsString()),
                    fare.get("price_cents").getAsInt() / 100.0,
                    fare.get("price_currency").getAsString()
            );

            return List.of(trip);
        }
    }
}
