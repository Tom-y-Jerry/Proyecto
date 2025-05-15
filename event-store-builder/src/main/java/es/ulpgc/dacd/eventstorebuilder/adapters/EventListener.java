package es.ulpgc.dacd.eventstorebuilder.adapters;
import javax.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventListener {
    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    private final Session session;
    private final EventHandler handler;

    public EventListener(Session session, EventHandler handler) {
        this.session = session;
        this.handler = handler;
    }

    public void subscribe(String topicName) {
        try {
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage textMsg) {
                    try {
                        handler.handle(topicName, textMsg.getText());
                    } catch (Exception e) {
                        log.error("Error handling event from topic {}: {}", topicName, e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to subscribe to topic " + topicName, e);
        }
    }

    public interface EventHandler {
        void handle(String topic, String rawJson) throws Exception;
    }
}

