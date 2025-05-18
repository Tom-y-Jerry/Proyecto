package es.ulpgc.dacd.ticketmasterfeeder.infrastructure.adapter;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class ActiveMQConnectionManager {
    private final String brokerUrl;

    public ActiveMQConnectionManager(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public Connection createConnection() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = factory.createConnection();
        connection.start();
        return connection;
    }

    public Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public MessageProducer createProducer(Session session, String topicName) throws JMSException {
        Destination topic = session.createTopic(topicName);
        return session.createProducer(topic);
    }
}
