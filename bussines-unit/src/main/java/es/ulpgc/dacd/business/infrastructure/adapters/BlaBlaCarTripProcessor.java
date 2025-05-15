package es.ulpgc.dacd.business.infrastructure.adapters;

import com.google.gson.*;
import es.ulpgc.dacd.business.infrastructure.ports.DatamartService;
import es.ulpgc.dacd.business.domain.Trip;
import es.ulpgc.dacd.business.infrastructure.ports.EventProcessor;

import java.time.Duration;
import java.time.Instant;

public class BlaBlaCarTripProcessor implements EventProcessor<Trip> {
    private final DatamartService datamart;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, type, context) -> new JsonPrimitive(src.toString()))
            .create();

    public BlaBlaCarTripProcessor(DatamartService datamart) {
        this.datamart = datamart;
    }

    @Override
    public void process(Trip trip) {
        try {
            long duration = Duration.between(trip.getDeparture(), trip.getArrival()).toMinutes();

            datamart.insertTrip(
                    trip.getOrigin(),
                    trip.getDestination(),
                    trip.getDeparture().toString(),
                    trip.getArrival().toString(),
                    trip.getPrice(),
                    trip.getCurrency(),
                    duration,
                    trip.getSs(),
                    gson.toJson(trip)
            );
        } catch (Exception e) {
            throw new RuntimeException("Error processing BlaBlaCar trip", e);
        }
    }
}
