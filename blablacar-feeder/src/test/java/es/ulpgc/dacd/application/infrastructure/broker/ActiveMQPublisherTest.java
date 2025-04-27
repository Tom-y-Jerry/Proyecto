package es.ulpgc.dacd.infrastructure.broker;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.*;
import javax.jms.*;

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
