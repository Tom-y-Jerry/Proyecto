package es.ulpgc.dacd.domain.port;

import es.ulpgc.dacd.domain.model.Fare;
import java.util.List;

public interface Fares {
    List<Fare> getAvailableFares();
}
