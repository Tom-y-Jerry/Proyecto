package es.ulpgc.dacd.business.infrastructure.adapters;
import com.google.gson.*;
import es.ulpgc.dacd.business.infrastructure.ports.EventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.*;
import java.lang.reflect.Type;
import java.time.Instant;

public class ActiveMQConsumer<T> {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMQConsumer.class);

    private final String topic;
    private final EventProcessor<T> processor;
    private final Class<T> type;
    private final Gson gson;
    private final ActiveMQConnectionManager connectionManager;

    public ActiveMQConsumer(String topic, String brokerUrl, EventProcessor<T> processor, Class<T> type) {
        this.topic = topic;
        this.processor = processor;
        this.type = type;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantDeserializer())
                .create();
        this.connectionManager = new ActiveMQConnectionManager(brokerUrl, topic);
    }

    public void start() {
        try {
            Session session = connectionManager.createSession();
            connectionManager.createConsumer(session, this::handleMessage);
            logger.info("Subscribed to topic: {}", topic);
        } catch (Exception e) {
            logger.error("Error connecting or subscribing to broker", e);
            throw new RuntimeException("Error connecting or subscribing to broker", e);
        }
    }

    private void handleMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String json = textMessage.getText();
                T item = gson.fromJson(json, type);
                processor.process(item);
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
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