package es.ulpgc.dacd;

import es.ulpgc.dacd.application.publisher.EventPublisher;
import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Events;
import es.ulpgc.dacd.domain.port.EventsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class TicketMasterServiceControllerTest {

    private Events mockEvents;
    private EventsRepository mockRepository;
    private EventPublisher controller;

    @BeforeEach
    void setUp() {
        mockEvents = mock(Events.class);
        mockRepository = mock(EventsRepository.class);
        //controller = new EventPublisher(mockEvents, mockRepository);
    }

    @Test
    void testRunStoresEventsCorrectly() {
        List<Event> sampleEvents = List.of(
                new Event("1", "Concierto", "2025-05-01", "Madrid"),
                new Event("2", "Festival", "2025-06-01", "Barcelona")
        );
        when(mockEvents.getCleanEvents()).thenReturn(sampleEvents);

        controller.run();

        verify(mockEvents).getCleanEvents();
        verify(mockRepository).saveAll(sampleEvents);
    }

    @Test
    void testRunWithNoEvents() {
        when(mockEvents.getCleanEvents()).thenReturn(List.of());

        controller.run();

        verify(mockEvents).getCleanEvents();
        verify(mockRepository).saveAll(List.of());
    }
}



