package es.ulpgc.dacd.domain.port;
import es.ulpgc.dacd.domain.model.Trip;
import java.util.List;

public interface Trips {
    List<Trip> getCleanTrips();
}
