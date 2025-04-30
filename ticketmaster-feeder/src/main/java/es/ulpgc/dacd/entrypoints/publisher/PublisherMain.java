package es.ulpgc.dacd.entrypoints.publisher;

import es.ulpgc.dacd.application.publisher.EventPublisher;
import es.ulpgc.dacd.infrastructure.adapter.TicketMasterEvents;
import es.ulpgc.dacd.infrastructure.api.*;
import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;

public final class PublisherMain {

    public static void main(String[] args) {
        try (ActiveMQPublisher mq = new ActiveMQPublisher("tcp://localhost:61616")) {

            new EventPublisher(
                    new TicketMasterEvents(new TicketMasterAPIClient()),
                    mq).run();

        } catch (Exception e) {
            System.err.println("❌ PublisherMain: " + e.getMessage());
        }
    }
}
