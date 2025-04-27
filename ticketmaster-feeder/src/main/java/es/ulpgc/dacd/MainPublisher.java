package es.ulpgc.dacd;

import es.ulpgc.dacd.application.TicketMasterPublisherController;
import es.ulpgc.dacd.infrastructure.api.TicketMasterAPIClient;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;

public class MainPublisher {
    public static void main(String[] args) {
        try {
            TicketMasterAPIClient apiClient = new TicketMasterAPIClient();
            ActiveMQPublisher publisher = new ActiveMQPublisher("tcp://localhost:61616");
            TicketMasterPublisherController controller = new TicketMasterPublisherController(apiClient, publisher);
            controller.run();
        } catch (Exception e) {
            System.err.println("❌ Error en MainPublisher: " + e.getMessage());
        }
    }
}
