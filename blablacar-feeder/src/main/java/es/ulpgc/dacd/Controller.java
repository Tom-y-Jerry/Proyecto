package es.ulpgc.dacd;

import es.ulpgc.dacd.domain.Trip;
import es.ulpgc.dacd.ports.TripProvider;
import es.ulpgc.dacd.ports.TripStorage;

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
                System.out.printf(formatTrip(trip));
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private String formatTrip(Trip trip) {
        double euros = trip.getPriceCents() / 100.0;

        String departure = trip.getDeparture().atZone(java.time.ZoneId.systemDefault())
                .toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        String arrival = trip.getArrival().atZone(java.time.ZoneId.systemDefault())
                .toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        java.time.Duration duration = java.time.Duration.between(trip.getDeparture(), trip.getArrival());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return String.format("Viaje: %s → %s | %s - %s | %dh %02dmin | %.2f %s\n",
                trip.getOrigin(),
                trip.getDestination(),
                departure,
                arrival,
                hours,
                minutes,
                euros,
                trip.getCurrency());
    }

}
