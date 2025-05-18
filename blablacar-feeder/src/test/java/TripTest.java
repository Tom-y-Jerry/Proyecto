import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    @Test
    void shouldCreateTripCorrectly() {
        Instant now = Instant.now();
        Trip trip = new Trip("blablacar", "Madrid", "Valencia", now, now.plusSeconds(3600), 15.0, "EUR");

        assertEquals("blablacar", trip.getSs());
        assertEquals("Madrid", trip.getOrigin());
        assertEquals("Valencia", trip.getDestination());
        assertEquals(now, trip.getDeparture());
        assertEquals(now.plusSeconds(3600), trip.getArrival());
        assertEquals(15.0, trip.getPrice());
        assertEquals("EUR", trip.getCurrency());
        assertNotNull(trip.getTs());
    }

    @Test
    void timestampShouldBeSetToNow() {
        Trip trip = new Trip("source", "A", "B", Instant.now(), Instant.now(), 5.0, "USD");
        Instant now = Instant.now();
        assertTrue(trip.getTs().isBefore(now) || trip.getTs().equals(now));
    }
}
