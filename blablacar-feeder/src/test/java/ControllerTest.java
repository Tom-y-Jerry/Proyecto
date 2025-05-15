import es.ulpgc.dacd.blablacarfeeder.Controller;
import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters.ActiveMqTripStorage;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.adapters.BlablacarTripProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class ControllerTest {

    private BlablacarTripProvider provider;
    private ActiveMqTripStorage storage;
    private Controller controller;

    @BeforeEach
    void setUp() {
        provider = mock(BlablacarTripProvider.class);
        storage = mock(ActiveMqTripStorage.class);
        controller = new Controller(provider, storage);
    }

    @Test
    void shouldPublishAllTrips() {
        List<Trip> fakeTrips = List.of(
            new Trip("ss", "A", "B", java.time.Instant.now(), java.time.Instant.now().plusSeconds(1000), 10, "EUR")
        );
        when(provider.get()).thenReturn(fakeTrips);

        controller.publishTrips();

        verify(provider, times(1)).get();
        verify(storage, times(1)).save(fakeTrips.get(0));
    }
}