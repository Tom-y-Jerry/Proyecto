package es.ulpgc.dacd.application.service;

import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.Stations;
import es.ulpgc.dacd.domain.port.StationsRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;

class StationServiceTest {

    @Test
    void shouldFetchAndSaveStations() {
        Stations stations = mock(Stations.class);
        StationsRepository repository = mock(StationsRepository.class);
        StationService service = new StationService(stations, repository);

        List<Station> stationList = List.of(mock(Station.class));
        when(stations.getCleanStations()).thenReturn(stationList);

        service.run();

        verify(stations).getCleanStations();
        verify(repository).saveAll(stationList);
    }
}
