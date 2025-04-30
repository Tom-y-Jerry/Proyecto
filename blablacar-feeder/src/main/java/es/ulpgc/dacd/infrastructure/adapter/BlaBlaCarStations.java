package es.ulpgc.dacd.infrastructure.adapter;
import es.ulpgc.dacd.domain.model.Station;
import es.ulpgc.dacd.domain.port.Stations;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarAPIClient;
import es.ulpgc.dacd.infrastructure.api.StationsParser;

import java.util.Collections;
import java.util.List;

public final class BlaBlaCarStations implements Stations {
    private final BlaBlaCarAPIClient apiClient;
    private final StationsParser parser = new StationsParser();

    public BlaBlaCarStations(BlaBlaCarAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Station> getCleanStations() {
        try {
            String json = apiClient.fetchStopsJson();
            return parser.parse(json);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo estaciones: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}


