import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters.ActiveMqTripStorage;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters.BlablacarTripProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

class ControllerTest {

    private BlablacarTripProvider provider;
    private ActiveMqTripStorage storage;

    @BeforeEach
    void setUp() {
        provider = mock(BlablacarTripProvider.class);
        storage = mock(ActiveMqTripStorage.class);
    }

    @Test
    void shouldSaveProvidedTrips() {
        List<Trip> trips = List.of(
                new Trip("ss", "A", "B", Instant.now(), Instant.now().plusSeconds(3000), 20.0, "EUR")
        );

        when(provider.provide()).thenReturn(trips);
        for (Trip trip : provider.provide()) {
            storage.save(trip);
        }
        verify(provider, times(1)).provide();
        verify(storage, times(1)).save(trips.get(0));
    }
}