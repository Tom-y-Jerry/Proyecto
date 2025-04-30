package es.ulpgc.dacd.application.publisher;


import com.google.gson.Gson;
import es.ulpgc.dacd.domain.event.TicketmasterEvent;
import es.ulpgc.dacd.domain.model.Event;
import es.ulpgc.dacd.infrastructure.api.TicketMasterEvents;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.*;

class EventPublisherTest {

    private TicketMasterEvents source;
    private ActiveMQPublisher broker;
    private EventPublisher publisher;

    @BeforeEach
    void setUp() {
        source = mock(TicketMasterEvents.class);
        broker = mock(ActiveMQPublisher.class);
        publisher = new EventPublisher(source, broker);
    }

    @Test
    void shouldPublishAllEventsAsJson() throws Exception {
        // Arrange
        Event fakeEvent = new Event("event-1", "title", "city", "2025-04-29T20:00");
        TicketmasterEvent expectedEvent = new TicketmasterEvent("feeder-ticketmaster", fakeEvent);

        when(source.getCleanEvents()).thenReturn(List.of(fakeEvent));
        doNothing().when(broker).publish(anyString(), anyString());

        // Act
        publisher.run();

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(broker).publish(eq("prediction.Events"), messageCaptor.capture());

        String jsonSent = messageCaptor.getValue();
        TicketmasterEvent actualEvent = new Gson().fromJson(jsonSent, TicketmasterEvent.class);
    }

}
