package es.ulpgc.dacd.business;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class BusinessConsumer {
    private final String topic;
    private final String brokerUrl;
    private final EventProcessor processor;

    public BusinessConsumer(String topic, String brokerUrl, EventProcessor processor) {
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
                    System.err.println("Error procesando mensaje: " + e.getMessage());
                }
            });

            System.out.println("Suscrito al topic: " + topic);

        } catch (Exception e) {
            throw new RuntimeException("Error conectando o suscribiéndose al broker", e);
        }
    }
}



