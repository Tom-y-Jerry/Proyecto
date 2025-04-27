package es.ulpgc.dacd.entrypoints.scheduler;

import es.ulpgc.dacd.application.service.StationService;
import es.ulpgc.dacd.application.service.FareService;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarAPIClient;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarBusAPIClient;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarBusFares;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarStations;
import es.ulpgc.dacd.infrastructure.persistence.SQLiteStationsRepository;
import es.ulpgc.dacd.infrastructure.persistence.SQLiteFaresRepository;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerMain {
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static void main(String[] args) {
        StationService stationService = createStationService();
        FareService fareService = createFareService();
        startScheduler(stationService, fareService);
    }

    private static StationService createStationService() {
        BlaBlaCarAPIClient apiClient = new BlaBlaCarAPIClient();
        BlaBlaCarStations stations = new BlaBlaCarStations(apiClient);
        SQLiteStationsRepository repository = new SQLiteStationsRepository(DB_URL);
        return new StationService(stations, repository);
    }

    private static FareService createFareService() {
        BlaBlaCarBusAPIClient busClient = new BlaBlaCarBusAPIClient();
        BlaBlaCarBusFares fares = new BlaBlaCarBusFares(busClient);
        SQLiteFaresRepository repository = new SQLiteFaresRepository(DB_URL);
        return new FareService(fares, repository);
    }

    private static void startScheduler(StationService stationService, FareService fareService) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(stationService::run, 0, 1, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(fareService::run, 0, 4, TimeUnit.HOURS);
    }
}

