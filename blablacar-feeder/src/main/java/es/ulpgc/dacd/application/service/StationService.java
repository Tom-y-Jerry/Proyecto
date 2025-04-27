package es.ulpgc.dacd.application.service;

import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.Stations;
import es.ulpgc.dacd.domain.port.StationsRepository;
import java.util.List;

public class StationService {
    private final Stations stations;
    private final StationsRepository repository;

    public StationService(Stations stations, StationsRepository repository) {
        this.stations = stations;
        this.repository = repository;
    }

    public void run() {
        List<Station> stationList = fetchStations();
        saveStations(stationList);
    }

    private List<Station> fetchStations() {
        return stations.getCleanStations();
    }

    private void saveStations(List<Station> stations) {
        repository.saveAll(stations);
    }
}
