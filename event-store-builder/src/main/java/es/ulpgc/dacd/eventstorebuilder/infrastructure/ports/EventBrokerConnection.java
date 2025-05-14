package es.ulpgc.dacd.eventstorebuilder.infrastructure.ports;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class EventBrokerConnection {
    private final Connection connection;
    private final Session session;

    public EventBrokerConnection(String brokerUrl, String clientId) {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            this.connection = factory.createConnection();
            this.connection.setClientID(clientId);
            this.connection.start();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to broker", e);
        }
    }

    public Session getSession() {
        return session;
    }

    public void close() {
        try {
            session.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("Error closing broker connection: " + e.getMessage());
        }
    }
}
