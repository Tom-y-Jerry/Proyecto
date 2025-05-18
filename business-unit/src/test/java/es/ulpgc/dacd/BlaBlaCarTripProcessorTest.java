package es.ulpgc.dacd;

import es.ulpgc.dacd.business.domain.Trip;
import es.ulpgc.dacd.business.infrastructure.adapters.Processor.BlaBlaCarTripProcessor;
import es.ulpgc.dacd.business.infrastructure.ports.DatamartService;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.mockito.Mockito.*;

class BlaBlaCarTripProcessorTest {

    @Test
    void shouldInsertTripInDatamart() {
        DatamartService datamart = mock(DatamartService.class);
        BlaBlaCarTripProcessor processor = new BlaBlaCarTripProcessor(datamart);

        Instant departure = Instant.parse("2025-05-17T08:00:00Z");
        Instant arrival = Instant.parse("2025-05-17T10:30:00Z");

        Trip trip = new Trip(
                "feeder-blablacar", "Madrid", "Barcelona", departure, arrival, 25.0, "EUR"
        );
        processor.process(trip);
        verify(datamart).insertTrip(
                eq("Madrid"),
                eq("Barcelona"),
                eq("2025-05-17T08:00:00Z"),
                eq("2025-05-17T10:30:00Z"),
                eq(25.0),
                eq("EUR"),
                eq(150L),
                eq("feeder-blablacar"),
                anyString()
        );
    }
}