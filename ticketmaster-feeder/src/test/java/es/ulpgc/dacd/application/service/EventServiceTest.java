package es.ulpgc.dacd.application.service;

import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.domain.port.Events;
import es.ulpgc.dacd.domain.port.EventsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class EventServiceTest {

    private Events source;
    private EventsRepository repository;
    private EventService service;

    @BeforeEach
    void setUp() {
        source = mock(Events.class);
        repository = mock(EventsRepository.class);
        service = new EventService(source, repository);
    }

    @Test
    void shouldFetchAndSaveEvents() {
        // Arrange
        Event fakeEvent = new Event("123", "Concierto", "2025-05-01T20:00", "Madrid");
        List<Event> events = List.of(fakeEvent);
        when(source.getCleanEvents()).thenReturn(events);

        // Act
        service.run();

        // Assert
        verify(source, times(1)).getCleanEvents();
        verify(repository, times(1)).saveAll(events);
    }
}
