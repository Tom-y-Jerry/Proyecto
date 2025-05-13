package es.ulpgc.dacd.eventstorebuilder;

import javax.jms.*;

public class EventListener {
    private final Session session;
    private final EventHandler handler;

    public EventListener(Session session, EventHandler handler) {
        this.session = session;
        this.handler = handler;
    }

    public void subscribe(String topicName) {
        try {
            Topic topic = session.createTopic(topicName);
            // 🔁 Cambiado: suscripción NO durable
            MessageConsumer consumer = session.createConsumer(topic);

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage textMsg) {
                    try {
                        System.out.println("📥 Mensaje recibido desde " + topicName + ": " + textMsg.getText());
                        handler.handle(topicName, textMsg.getText());
                    } catch (Exception e) {
                        System.err.println("❌ Error handling event: " + e.getMessage());
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
