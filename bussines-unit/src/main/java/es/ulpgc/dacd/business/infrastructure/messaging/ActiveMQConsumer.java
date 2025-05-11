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
            Connection connection = createConnection();
            Session session = createSession(connection);
            subscribeToTopic(session);
        } catch (Exception e) {
            throw new RuntimeException("Error connecting or subscribing to broker", e);
        }
    }

    private Connection createConnection() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = factory.createConnection();
        connection.setClientID("business-unit-" + topic);
        connection.start();
        return connection;
    }

    private Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void subscribeToTopic(Session session) throws JMSException {
        Topic destination = session.createTopic(topic);
        MessageConsumer consumer = session.createDurableSubscriber(destination, topic + "-business-subscriber");
        consumer.setMessageListener(this::handleMessage);
        System.out.println("Subscribed to topic: " + topic);
    }

    private void handleMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                processor.process(textMessage.getText());
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}