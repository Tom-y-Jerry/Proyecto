package es.ulpgc.dacd.business.infrastructure.adapters;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMQConnectionManager {
    private final String brokerUrl;
    private final String topic;

    private Connection connection;
    private Session session;

    public ActiveMQConnectionManager(String brokerUrl, String topic) {
        this.brokerUrl = brokerUrl;
        this.topic = topic;
    }

    public Session createSession() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        this.connection = factory.createConnection();
        this.connection.start();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        return this.session;
    }

    public MessageConsumer createConsumer(Session session, MessageListener listener) throws JMSException {
        Topic destination = session.createTopic(topic);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(listener);
        return consumer;
    }

    public void close() {
        try {
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
