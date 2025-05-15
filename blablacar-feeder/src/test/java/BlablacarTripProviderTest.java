    import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters.BlablacarTripProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlablacarTripProviderTest {

    private BlablacarTripProvider provider;

    @BeforeEach
    void setUp() {
        provider = new BlablacarTripProvider("", "", "");
    }

    @Test
    void shouldReturnNonEmptyTripList() {
        List<Trip> trips = provider.provide();

        assertNotNull(trips);
        assertFalse(trips.isEmpty());
    }

    @Test
    void tripsShouldHaveValidTimestamps() {
        List<Trip> trips = provider.provide();
        Instant now = Instant.now();
        for (Trip trip : trips) {
            assertTrue(trip.getDeparture().isBefore(trip.getArrival()));
            assertTrue(trip.getTs().isBefore(now) || trip.getTs().equals(now));
        }
    }
}