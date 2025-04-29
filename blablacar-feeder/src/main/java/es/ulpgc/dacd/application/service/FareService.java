package es.ulpgc.dacd.application.service;

import es.ulpgc.dacd.domain.model.Fare;
import es.ulpgc.dacd.domain.port.Fares;
import es.ulpgc.dacd.domain.port.FaresRepository;
import java.util.List;

public final class FareService {
    private final Fares fares;
    private final FaresRepository repository;

    public FareService(Fares fares, FaresRepository repository) {
        this.fares = fares;
        this.repository = repository;
    }

    public void run() {
        List<Fare> fareList = fetchFares();
        saveFares(fareList);
    }

    private List<Fare> fetchFares() {
        return fares.getAvailableFares();
    }

    private void saveFares(List<Fare> fares) {
        repository.saveAll(fares);
    }
}

