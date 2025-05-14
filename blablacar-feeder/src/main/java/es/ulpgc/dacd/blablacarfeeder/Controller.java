package es.ulpgc.dacd.blablacarfeeder;
import es.ulpgc.dacd.blablacarfeeder.domain.Trip;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.ports.TripProvider;
import es.ulpgc.dacd.blablacarfeeder.infrastructure.ports.TripStorage;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    private final TripProvider tripProvider;
    private final TripStorage store;

    public Controller(TripProvider tripProvider, TripStorage store) {
        this.tripProvider = tripProvider;
        this.store = store;
    }

    public void execute() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Ejecutando consulta de viajes...");
            List<Trip> trips = tripProvider.provide();
            for (Trip trip : trips) {
                store.save(trip);
            }
        }, 0, 1, TimeUnit.HOURS);
    }

}
