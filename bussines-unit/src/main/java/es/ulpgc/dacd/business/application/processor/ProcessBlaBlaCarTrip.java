package es.ulpgc.dacd.business.application.processor;

import es.ulpgc.dacd.business.application.service.DatamartService;
import es.ulpgc.dacd.business.domain.model.Trip;
import es.ulpgc.dacd.business.application.processor.EventProcessor;

import java.time.Duration;

public class ProcessBlaBlaCarTrip implements EventProcessor<Trip> {
    private final DatamartService datamart;

    public ProcessBlaBlaCarTrip(DatamartService datamart) {
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
                    trip.toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error processing BlaBlaCar trip", e);
        }
    }
}
