package es.ulpgc.dacd.business;


import es.ulpgc.dacd.business.domain.Event;
import es.ulpgc.dacd.business.domain.Trip;
import es.ulpgc.dacd.business.infrastructure.adapters.*;
import es.ulpgc.dacd.business.infrastructure.ports.DatamartService;

public class Controller {
    private final DatamartService datamart;
    private final String brokerUrl;

    public Controller(DatamartService datamart, String brokerUrl) {
        this.datamart = datamart;
        this.brokerUrl = brokerUrl;
    }

    public void subscribeToTopics() {
        subscribe("Events", new TicketmasterEventProcessor(datamart), Event.class);
        subscribe("Trips", new BlaBlaCarTripProcessor(datamart), Trip.class);
    }

    public void loadHistoricalEvents() {
        processHistorical("eventstore/Events/feeder-ticketmaster", new TicketmasterEventProcessor(datamart), Event.class);
        processHistorical("eventstore/Trips/feeder-blablacar", new BlaBlaCarTripProcessor(datamart), Trip.class);
    }

    private <T> void subscribe(String topic, EventProcessor<T> processor, Class<T> type) {
        new ActiveMQConsumer<>(topic, brokerUrl, processor, type).start();
    }

    private <T> void processHistorical(String path, EventProcessor<T> processor, Class<T> type) {
        new HistoricalEventLoader<>(processor, type).loadFromDirectory(path);
    }
}