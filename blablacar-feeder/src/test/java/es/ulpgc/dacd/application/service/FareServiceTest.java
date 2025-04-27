package es.ulpgc.dacd.application.service;

import es.ulpgc.dacd.domain.model.Fare;
import es.ulpgc.dacd.domain.port.Fares;
import es.ulpgc.dacd.domain.port.FaresRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;

class FareServiceTest {

    @Test
    void shouldFetchAndSaveFares() {
        Fares fares = mock(Fares.class);
        FaresRepository repository = mock(FaresRepository.class);
        FareService service = new FareService(fares, repository);

        List<Fare> fareList = List.of(mock(Fare.class));
        when(fares.getAvailableFares()).thenReturn(fareList);

        service.run();

        verify(fares).getAvailableFares();
        verify(repository).saveAll(fareList);
    }
}
