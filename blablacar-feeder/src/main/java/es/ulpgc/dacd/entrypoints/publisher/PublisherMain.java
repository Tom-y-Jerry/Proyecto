package es.ulpgc.dacd.entrypoints.publisher;

import es.ulpgc.dacd.application.publisher.StationEventPublisher;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarAPIClient;
import es.ulpgc.dacd.infrastructure.adapter.BlaBlaCarStations;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;

public final class PublisherMain {
    public static void main(String[] args) {
        try {
            StationEventPublisher publisher = createPublisher();
            publisher.run();
        } catch (Exception e) {
            System.err.println("❌ Error al publicar eventos: " + e.getMessage());
        }
    }

    private static StationEventPublisher createPublisher() throws Exception {
        BlaBlaCarAPIClient apiClient = new BlaBlaCarAPIClient();
        BlaBlaCarStations stations = new BlaBlaCarStations(apiClient);
        ActiveMQPublisher publisher = new ActiveMQPublisher("tcp://localhost:61616");
        return new StationEventPublisher(stations, publisher);
    }
}
