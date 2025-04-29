package es.ulpgc.dacd.infrastructure.broker;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public final class ActiveMQPublisher implements AutoCloseable {
    private final Connection connection;
    private final Session session;

    public ActiveMQPublisher(String brokerUrl) throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        this.connection = factory.createConnection();
        this.connection.start();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void publish(String topic, String jsonMessage) throws JMSException {
        Destination destination = session.createTopic(topic);
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage(jsonMessage);
        producer.send(message);
    }

    public void close() throws JMSException {
        session.close();
        connection.close();
    }
}
