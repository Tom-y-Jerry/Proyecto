package es.ulpgc.dacd.business.infrastructure.adapters;

import com.google.gson.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.lang.reflect.Type;
import java.time.Instant;

public class ActiveMQConsumer<T> {
    private final String topic;
    private final String brokerUrl;
    private final EventProcessor<T> processor;
    private final Class<T> type;
    private final Gson gson;

    public ActiveMQConsumer(String topic, String brokerUrl, EventProcessor<T> processor, Class<T> type) {
        this.topic = topic;
        this.brokerUrl = brokerUrl;
        this.processor = processor;
        this.type = type;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantDeserializer())
                .create();
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
                T item = gson.fromJson(json, type);
                processor.process(item);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private static class InstantDeserializer implements JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.parse(json.getAsString());
        }
    }
}