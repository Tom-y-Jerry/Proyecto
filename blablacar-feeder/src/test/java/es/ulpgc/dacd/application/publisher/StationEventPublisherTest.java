package es.ulpgc.dacd.application.publisher;

import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.Stations;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;

class StationEventPublisherTest {

    @Test
    void shouldPublishStationEvents() throws Exception {
        Stations stations = mock(Stations.class);
        ActiveMQPublisher publisher = mock(ActiveMQPublisher.class);
        StationEventPublisher eventPublisher = new StationEventPublisher(stations, publisher);

        Station station = mock(Station.class);
        when(station.getId()).thenReturn(1);
        when(station.getLongName()).thenReturn("Madrid");
        when(station.getLatitude()).thenReturn(40.4168);
        when(station.getLongitude()).thenReturn(-3.7038);
        when(stations.getCleanStations()).thenReturn(List.of(station));

        eventPublisher.run();

        verify(stations).getCleanStations();
        verify(publisher, atLeastOnce()).publish(eq("prediction.Stations"), anyString());
        verify(publisher).close();
    }
}
