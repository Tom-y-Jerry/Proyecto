package es.ulpgc.dacd.domain;

import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TripTest {

    @Test
    public void shouldCreateTripWithCorrectFields() {
        Instant departure = Instant.parse("2025-05-06T10:00:00Z");
        Instant arrival = Instant.parse("2025-05-06T12:00:00Z");
        Trip trip = new Trip("feeder-blablacar", "Las Palmas", "Tenerife", departure, arrival, 15.5, "EUR");

        assertEquals("feeder-blablacar", trip.getSs());
        assertEquals("Las Palmas", trip.getOrigin());
        assertEquals("Tenerife", trip.getDestination());
        assertEquals(departure, trip.getDeparture());
        assertEquals(arrival, trip.getArrival());
        assertEquals(15.5, trip.getPrice());
        assertEquals("EUR", trip.getCurrency());
        assertNotNull(trip.getTs());
    }
}
