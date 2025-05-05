package es.ulpgc.dacd.infrastructure.adapters;

import com.google.gson.JsonObject;
import es.ulpgc.dacd.domain.Trip;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class BlablacarTripProviderTest {

    @Test
    public void shouldMapJsonToTripCorrectly() {
        JsonObject fare = new JsonObject();
        fare.addProperty("departure", "2025-05-06T10:00:00Z");
        fare.addProperty("arrival", "2025-05-06T13:30:00Z");
        fare.addProperty("price_cents", 2450);
        fare.addProperty("price_currency", "EUR");

        BlablacarTripProvider provider = new BlablacarTripProvider(null, null, null, 0);
        Trip trip = new Trip(
                "feeder-blablacar", "Las Palmas", "Tenerife",
                Instant.parse(fare.get("departure").getAsString()),
                Instant.parse(fare.get("arrival").getAsString()),
                fare.get("price_cents").getAsInt() / 100.0,
                fare.get("price_currency").getAsString()
        );

        assertEquals("feeder-blablacar", trip.getSs());
        assertEquals("Las Palmas", trip.getOrigin());
        assertEquals("Tenerife", trip.getDestination());
        assertEquals(24.5, trip.getPrice(), 0.01);
        assertEquals("EUR", trip.getCurrency());
    }
}
