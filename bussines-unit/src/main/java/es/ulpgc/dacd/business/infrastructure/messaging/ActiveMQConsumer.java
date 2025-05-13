package es.ulpgc.dacd.business.infrastructure.messaging;

import es.ulpgc.dacd.business.application.processor.EventProcessor;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMQConsumer<T> {
    private final String topic;
    private final String brokerUrl;
    private final EventProcessor<T> processor;
    private final Class<T> type; // Para saber si es Trip o Event

    public ActiveMQConsumer(String topic, String brokerUrl, EventProcessor<T> processor, Class<T> type) {
        this.topic = topic;
        this.brokerUrl = brokerUrl;
        this.processor = processor;
        this.type = type;
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
                String json = textMessage.getText();
                T item = new com.google.gson.Gson().fromJson(json, type);
                processor.process(item);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}