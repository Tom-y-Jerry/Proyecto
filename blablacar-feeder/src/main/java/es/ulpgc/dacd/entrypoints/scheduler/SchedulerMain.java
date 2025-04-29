package es.ulpgc.dacd.entrypoints.scheduler;

import es.ulpgc.dacd.application.service.StationService;
import es.ulpgc.dacd.application.service.FareService;
import es.ulpgc.dacd.application.service.TripService;
import es.ulpgc.dacd.domain.port.Trips;
import es.ulpgc.dacd.domain.port.TripsRepository;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarAPIClient;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarBusAPIClient;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarBusFares;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarStations;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarTrips;
import es.ulpgc.dacd.infrastructure.persistence.SQLiteStationsRepository;
import es.ulpgc.dacd.infrastructure.persistence.SQLiteFaresRepository;
import es.ulpgc.dacd.infrastructure.persistence.SQLiteTripsRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SchedulerMain {
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static void main(String[] args) {
        StationService stationService = createStationService();
        FareService fareService = createFareService();
        TripService tripService = createTripService();

        startScheduler(stationService, fareService, tripService);
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

    private static TripService createTripService() {
        Trips trips = new BlaBlaCarTrips(DB_URL);
        TripsRepository repository = new SQLiteTripsRepository(DB_URL);
        return new TripService(trips, repository);
    }

    private static void startScheduler(StationService stationService, FareService fareService, TripService tripService) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            stationService.run();   // 1. Guarda estaciones
            fareService.run();      // 2. Guarda fares
            tripService.run();      // 3. Guarda trips con nombres
        }, 0, 2, TimeUnit.HOURS);
    }
}



