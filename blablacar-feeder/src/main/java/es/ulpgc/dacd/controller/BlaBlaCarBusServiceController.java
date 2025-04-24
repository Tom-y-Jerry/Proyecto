package es.ulpgc.dacd.controller;

import es.ulpgc.dacd.domain.model.Fare;
import es.ulpgc.dacd.infrastructure.adapter.SQLiteFaresRepository;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarBusAPIClient;
import es.ulpgc.dacd.infrastructure.api.FaresParser;

import java.util.List;

public class BlaBlaCarBusServiceController {
    private final BlaBlaCarBusAPIClient client;
    private final SQLiteFaresRepository repository;

    public BlaBlaCarBusServiceController(BlaBlaCarBusAPIClient client, SQLiteFaresRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    public void run() {
        System.out.println("Ejecutando BlaBlaCarBusService...");
        try {
            String json = client.fetchFaresByDate("2025-08-01");
            List<Fare> fares = FaresParser.parse(json);
            repository.saveAll(fares);
            System.out.println("Guardadas " + fares.size() + " tarifas en la base de datos.");
        } catch (Exception e) {
            System.err.println("❌ Error al consultar y guardar fares:");
            e.printStackTrace();
        }
    }
}
