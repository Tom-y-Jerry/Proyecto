package es.ulpgc.dacd.eventstorebuilder.adapters;

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
}
