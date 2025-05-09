package es.ulpgc.dacd.business.infrastructure.messaging;

import es.ulpgc.dacd.business.application.processor.EventProcessor;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMQConsumer {
    private final String topic;
    private final String brokerUrl;
    private final EventProcessor processor;

    public ActiveMQConsumer(String topic, String brokerUrl, EventProcessor processor) {
        this.topic = topic;
        this.brokerUrl = brokerUrl;
        this.processor = processor;
    }

    public void start() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.setClientID("business-unit-" + topic);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic destination = session.createTopic(topic);
            MessageConsumer consumer = session.createDurableSubscriber(destination, topic + "-business-subscriber");

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage textMessage) {
                        String raw = textMessage.getText();
                        processor.process(raw);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
                }
            });

            System.out.println("Subscribed to topic: " + topic);

        } catch (Exception e) {
            throw new RuntimeException("Error connecting or subscribing to broker", e);
        }
    }
}
