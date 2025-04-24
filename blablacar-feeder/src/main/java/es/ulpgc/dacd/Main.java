package es.ulpgc.dacd;

import es.ulpgc.dacd.controller.BlaBlaCarBusServiceController;
import es.ulpgc.dacd.controller.BlaBlaCarServiceController;
import es.ulpgc.dacd.domain.port.Stations;
import es.ulpgc.dacd.domain.port.StationsRepository;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarAPIClient;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarBusAPIClient;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarStations;
import es.ulpgc.dacd.infrastructure.adapter.SQLiteStationsRepository;
import es.ulpgc.dacd.infrastructure.adapter.SQLiteFaresRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static void main(String[] args) {
        // Servicio de paradas (GET /v3/stops)
        BlaBlaCarAPIClient apiClient = new BlaBlaCarAPIClient();
        Stations stations = new BlaBlaCarStations(apiClient);
        StationsRepository stationsRepository = new SQLiteStationsRepository(DB_URL);
        BlaBlaCarServiceController stationService = new BlaBlaCarServiceController(stations, stationsRepository);

        // Servicio de fares (GET /v3/fares)
        BlaBlaCarBusAPIClient busClient = new BlaBlaCarBusAPIClient();
        SQLiteFaresRepository faresRepository = new SQLiteFaresRepository(DB_URL);
        BlaBlaCarBusServiceController faresService = new BlaBlaCarBusServiceController(busClient, faresRepository);

        // Planificación: estaciones cada hora, fares cada 4 horas
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        scheduler.scheduleAtFixedRate(stationService::run, 0, 1, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(faresService::run, 0, 4, TimeUnit.HOURS);
    }
}

