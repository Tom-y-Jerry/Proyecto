package es.ulpgc.dacd;

import es.ulpgc.dacd.business.domain.Event;
import es.ulpgc.dacd.business.infrastructure.adapters.Processor.TicketmasterEventProcessor;
import es.ulpgc.dacd.business.infrastructure.ports.DatamartService;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.*;

class TicketMasterEventProcessorTest {

    @Test
    void shouldInsertEventInDatamart() {
        DatamartService datamart = mock(DatamartService.class);
        TicketmasterEventProcessor processor = new TicketmasterEventProcessor(datamart);

        Event event = new Event("feeder-ticketmaster", "e1", "Concert", Instant.now(), "20:00", "Madrid"
        );
        processor.process(event);
        verify(datamart).insertEvent(
                eq("e1"),
                eq("Concert"),
                anyString(),
                eq("20:00"),
                eq("Madrid"),
                eq("feeder-ticketmaster"),
                anyString()
        );
    }
}
