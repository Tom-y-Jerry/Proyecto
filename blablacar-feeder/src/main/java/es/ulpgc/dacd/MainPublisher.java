package es.ulpgc.dacd;

import es.ulpgc.dacd.controller.BlaBlaCarPublisherController;
import es.ulpgc.dacd.infrastructure.api.BlaBlaCarAPIClient;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;

public class MainPublisher {
    public static void main(String[] args) {
        try {
            BlaBlaCarAPIClient apiClient = new BlaBlaCarAPIClient();
            ActiveMQPublisher publisher = new ActiveMQPublisher("tcp://localhost:61616");

            // ⬇ Usa controller que a su vez usa BlaBlaCarStations (filtra correctamente)
            BlaBlaCarPublisherController controller = new BlaBlaCarPublisherController(apiClient, publisher);
            controller.run();

        } catch (Exception e) {
            System.err.println("❌ Error en MainPublisher: " + e.getMessage());
        }
    }
}
