package es.ulpgc.dacd.domain.port;

import es.ulpgc.dacd.domain.model.Fare;
import java.util.List;

public interface FaresRepository {
    void save(Fare fare);
    void saveAll(List<Fare> fares);
    List<Fare> findAll();
}
