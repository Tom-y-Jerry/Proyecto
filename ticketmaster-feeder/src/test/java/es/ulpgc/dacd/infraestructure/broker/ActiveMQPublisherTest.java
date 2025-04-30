package es.ulpgc.dacd.infraestructure.broker;

import es.ulpgc.dacd.infrastructure.broker.ActiveMQPublisher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.apache.activemq.broker.BrokerService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ActiveMQPublisherTest {
    private static BrokerService broker;

    @BeforeAll
    static void setUpBroker() throws Exception {
        broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistent(false);
        broker.start();
    }

    @AfterAll
    static void tearDownBroker() throws Exception {
        broker.stop();
    }

    @Test
    void shouldPublishMessageToBroker() {
        assertDoesNotThrow(() -> {
            ActiveMQPublisher publisher = new ActiveMQPublisher("tcp://localhost:61616");
            publisher.publish("test.topic", "{\"message\":\"Hello World\"}");
            publisher.close();
        });
    }
}