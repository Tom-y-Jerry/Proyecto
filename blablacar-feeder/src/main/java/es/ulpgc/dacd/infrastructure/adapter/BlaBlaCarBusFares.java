package es.ulpgc.dacd.infrastructure.adapter;

import es.ulpgc.dacd.domain.model.Fare;
import es.ulpgc.dacd.domain.port.Fares;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarBusAPIClient;
import es.ulpgc.dacd.infrastructure.api.FaresParser;

import java.util.Collections;
import java.util.List;

public final class BlaBlaCarBusFares implements Fares {
    private final BlaBlaCarBusAPIClient apiClient;

    public BlaBlaCarBusFares(BlaBlaCarBusAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Fare> getAvailableFares() {
        try {
            String json = apiClient.fetchFaresByDate("2025-08-01"); // fecha fija por ahora
            return FaresParser.parse(json);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo fares: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
