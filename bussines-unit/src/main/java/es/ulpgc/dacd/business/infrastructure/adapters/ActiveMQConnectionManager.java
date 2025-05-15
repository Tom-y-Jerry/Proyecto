package es.ulpgc.dacd.business.infrastructure.adapters;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMQConnectionManager {
    private final String brokerUrl;
    private final String topic;

    public ActiveMQConnectionManager(String brokerUrl, String topic) {
        this.brokerUrl = brokerUrl;
        this.topic = topic;
    }

    public Session createSession() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = factory.createConnection();
        connection.setClientID("business-unit-" + topic);
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public MessageConsumer createConsumer(Session session, MessageListener listener) throws JMSException {
        Topic destination = session.createTopic(topic);
        MessageConsumer consumer = session.createDurableSubscriber(destination, topic + "-business-subscriber");
        consumer.setMessageListener(listener);
        return consumer;
    }
}

